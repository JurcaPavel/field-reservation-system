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
import cz.jurca.fieldreservationsystem.domain.NewOrUpdatedSportsField
import cz.jurca.fieldreservationsystem.domain.Page
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsField
import cz.jurca.fieldreservationsystem.domain.SportsFieldFilter
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UnloadedFilteredPage
import cz.jurca.fieldreservationsystem.domain.UnvalidatedSportsFieldId
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import kotlinx.coroutines.reactor.awaitSingle
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
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    suspend fun findSportsField(id: UnvalidatedSportsFieldId) = sportsFieldRepository.findById(id.value)?.run { SportsFieldId(this.getDaoId(), this@SportsFieldDbAdapter::getDetail) }

    suspend fun getDetail(id: SportsFieldId): SportsField =
        requireNotNull(sportsFieldRepository.findById(id.value)).let { dao ->
            SportsField(
                id = SportsFieldId(dao.getDaoId(), this::getDetail),
                name = Name(dao.name),
                sportTypes = findSportTypesOfSportsField(id.value),
                description = dao.description?.let { Description(it) },
                address = Address(City(dao.city), Street(dao.street), ZipCode(dao.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(dao.countryCode)))),
                coordinates = Coordinates(Latitude(dao.latitude), Longitude(dao.longitude)),
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
            items = results.map { dao -> dao.toDomain(this::getDetail, findSportTypesOfSportsField(dao.getDaoId())) },
            totalItems = totalItemsCount.toInt(),
        )
    }

    @Transactional
    suspend fun create(newSportsField: NewOrUpdatedSportsField): SportsField {
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
                ),
            )
        sportTypeRepository.findAllByNameIn(newSportsField.sportTypes.map { sportType -> sportType.name })
            .forEach { sportTypeDao ->
                sportsFieldSportTypeRepository.save(
                    SportsFieldSportTypeDao(
                        sportsFieldDao.getDaoId(),
                        sportTypeDao.getDaoId(),
                    ),
                )
            }

        return sportsFieldDao.toDomain(this::getDetail, newSportsField.sportTypes)
    }

    suspend fun update(updatedSportsField: NewOrUpdatedSportsField): SportsField = TODO()

    private suspend fun findSportTypesOfSportsField(sportsFieldId: Int): List<SportType> =
        sportTypeRepository.findAllBySportsFieldId(sportsFieldId).let { sportTypeDaos ->
            sportTypeDaos.map { sportTypeDao ->
                sportTypeDao.toDomain()
            }
        }
}