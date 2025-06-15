package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.codegen.types.SortByDirection
import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Coordinates
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Latitude
import cz.jurca.fieldreservationsystem.domain.Longitude
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NewSportsField
import cz.jurca.fieldreservationsystem.domain.Page
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsField
import cz.jurca.fieldreservationsystem.domain.SportsFieldFilter
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UnloadedFilteredPage
import cz.jurca.fieldreservationsystem.domain.UnvalidatedSportsFieldId
import cz.jurca.fieldreservationsystem.domain.UpdatedSportsField
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldDaoId
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SportsFieldDbAdapter(
    private val sportsFieldRepository: SportsFieldRepository,
    private val sportTypeRepository: SportTypeRepository,
    private val sportsFieldSportTypeRepository: SportsFieldSportTypeRepository,
    private val userDbAdapter: UserDbAdapter,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : SportsFieldDeletable {
    @Autowired
    @Lazy
    private lateinit var self: SportsFieldDeletable

    internal fun getId(sportsFieldDaoId: SportsFieldDaoId): SportsFieldId = SportsFieldId(sportsFieldDaoId.value, this@SportsFieldDbAdapter::getDetail, self::delete)

    suspend fun findSportsField(id: UnvalidatedSportsFieldId): SportsFieldId? =
        sportsFieldRepository.findById(id.value)
            ?.run { SportsFieldId(this.getDaoId().value, this@SportsFieldDbAdapter::getDetail, self::delete) }

    suspend fun getDetail(id: SportsFieldId): SportsField =
        requireNotNull(sportsFieldRepository.findById(id.value)).let { dao ->
            SportsField(
                id = id,
                name = Name(dao.name),
                sportTypes = findSportTypesOfSportsField(SportsFieldDaoId(id.value)),
                description = dao.description?.let { Description(it) },
                address = Address(City(dao.city), Street(dao.street), ZipCode(dao.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(dao.countryCode)))),
                coordinates = Coordinates(Latitude(dao.latitude), Longitude(dao.longitude)),
                managerId = UserId(dao.managerId, userDbAdapter::getDetail),
            )
        }

    suspend fun filterSportsFields(page: UnloadedFilteredPage<SportsField, SportsFieldFilter, UnloadedFilteredPage.SportsFieldSortBy>): Page<SportsField> {
        var criteria: Criteria = Criteria.empty()
        page.filters?.city?.let { city ->
            criteria = criteria.and(Criteria.where("city").`is`(city.value))
        }
        page.filters?.countryCode?.let { countryCode ->
            criteria = criteria.and(Criteria.where("country_code").`is`(countryCode.value))
        }
        // todo later implement sport types by changing from criteria to custom sql query
//        page.filters?.sportTypes?.let { sportTypes ->
//            criteria = criteria.and(Criteria.where("sport_type_name").`in`(sportTypes.map { it.name }))
//        }

        val sort: Sort? =
            page.sortBy?.let { sortBy ->
                if (sortBy.direction == SortByDirection.ASC) {
                    Sort.by(Sort.Direction.ASC, sortBy.field.name)
                } else {
                    Sort.by(Sort.Direction.DESC, sortBy.field.name)
                }
            }

        // constructor is zero based, so we need to subtract 1 from pageNumber
        val pageable = PageRequest.of(page.pageNumber - 1, page.pageSize)
        val query: Query = Query.query(criteria).with(pageable).sort(sort ?: Sort.unsorted())
        val countQuery: Query = Query.query(criteria)

        val results = r2dbcEntityTemplate.select(query, SportsFieldDao::class.java).collectList().awaitSingle()
        val totalItemsCount = r2dbcEntityTemplate.count(countQuery, SportsFieldDao::class.java).awaitSingle()

        return Page(
            items = results.map { dao -> dao.toDomain(this::findSportsField, userDbAdapter::getDetail, findSportTypesOfSportsField(dao.getDaoId())) },
            totalItems = totalItemsCount.toInt(),
        )
    }

    @Transactional
    suspend fun create(newSportsField: NewSportsField): SportsField {
        val sportsFieldDao =
            sportsFieldRepository.save(
                SportsFieldDao(
                    newSportsField.name.value,
                    newSportsField.coordinates.latitude.value,
                    newSportsField.coordinates.longitude.value,
                    newSportsField.address.city.value,
                    newSportsField.address.street.value,
                    newSportsField.address.zipCode.value,
                    newSportsField.address.country.alphaCode3.value,
                    newSportsField.description?.value,
                    newSportsField.loginUser.id.value,
                ),
            )
        sportTypeRepository.findAllByNameIn(newSportsField.sportTypes.map { sportType -> sportType.name })
            .forEach { sportTypeDao ->
                sportsFieldSportTypeRepository.save(
                    SportsFieldSportTypeDao(
                        sportsFieldDao.getDaoId().value,
                        sportTypeDao.getDaoId().value,
                    ),
                )
            }

        return sportsFieldDao.toDomain(this::findSportsField, userDbAdapter::getDetail, newSportsField.sportTypes)
    }

    @Transactional
    suspend fun update(updatedSportsField: UpdatedSportsField): SportsField =
        requireNotNull(sportsFieldRepository.findById(updatedSportsField.id.value)).let { sportsFieldDao ->
            sportsFieldDao.name = updatedSportsField.name.value
            sportsFieldDao.latitude = updatedSportsField.coordinates.latitude.value
            sportsFieldDao.longitude = updatedSportsField.coordinates.longitude.value
            sportsFieldDao.city = updatedSportsField.address.city.value
            sportsFieldDao.street = updatedSportsField.address.street.value
            sportsFieldDao.zipCode = updatedSportsField.address.zipCode.value
            sportsFieldDao.countryCode = updatedSportsField.address.country.alphaCode3.value
            sportsFieldDao.description = updatedSportsField.description?.value
            sportsFieldRepository.save(sportsFieldDao)
            sportsFieldDao.toDomain(this::findSportsField, userDbAdapter::getDetail, findSportTypesOfSportsField(sportsFieldDao.getDaoId()))
        }

    @Transactional
    override suspend fun delete(sportsFieldId: SportsFieldId): Unit =
        requireNotNull(sportsFieldRepository.findById(sportsFieldId.value)).let { sportsFieldDao ->
            sportsFieldSportTypeRepository.deleteAllBySportsFieldId(sportsFieldDao.getDaoId().value)
            sportsFieldDao
        }.let { sportsFieldDao ->
            sportsFieldRepository.delete(sportsFieldDao)
        }

    suspend fun isSportsFieldOwner(
        userId: UserId,
        sportsFieldId: SportsFieldId,
    ): Boolean = requireNotNull(sportsFieldRepository.findById(sportsFieldId.value)).managerId == userId.value

    private suspend fun findSportTypesOfSportsField(sportsFieldDaoId: SportsFieldDaoId): List<SportType> =
        sportTypeRepository.findAllBySportsFieldId(sportsFieldDaoId.value).let { sportTypeDaos ->
            sportTypeDaos.map { sportTypeDao ->
                sportTypeDao.toDomain()
            }
        }
}