package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.Page
import cz.jurca.fieldreservationsystem.domain.SportsField
import cz.jurca.fieldreservationsystem.domain.SportsFieldFilter
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UnloadedFilteredPage
import cz.jurca.fieldreservationsystem.domain.UnvalidatedSportsFieldId
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import org.springframework.stereotype.Component

@Component
class SportsFieldDbAdapter(
    private val sportsFieldRepository: SportsFieldRepository,
    private val sportsFieldSportTypeRepository: SportsFieldSportTypeRepository,
    private val sportTypeRepository: SportTypeRepository,
) {
    suspend fun findSportsField(id: UnvalidatedSportsFieldId) = sportsFieldRepository.findById(id.value)?.run { SportsFieldId(this.getDaoId(), this@SportsFieldDbAdapter::getDetail) }

    suspend fun getDetail(id: SportsFieldId): SportsField =
        requireNotNull(sportsFieldRepository.findById(id.value)).let { dao ->
            SportsField(
                id = SportsFieldId(dao.getDaoId(), this::getDetail),
                name = Name(dao.name),
                sportTypes =
                    sportsFieldSportTypeRepository.findBySportsFieldId(id.value)
                        .map { sportsFieldSportTypeDao ->
                            requireNotNull(sportTypeRepository.findById(sportsFieldSportTypeDao.sportTypeId)).toDomain()
                        },
                description = dao.description?.let { Description(it) },
                address = Address(City(dao.city), Street(dao.street), ZipCode(dao.zipCode), requireNotNull(Country.findByCode(dao.countryCode))),
                latitude = dao.latitude,
                longitude = dao.longitude,
            )
        }

    suspend fun filterSportsFields(page: UnloadedFilteredPage<SportsFieldId, SportsFieldFilter, UnloadedFilteredPage.SportsFieldSortBy>): Page<SportsFieldId> {
        return TODO("IMPLEMENT ME")
    }
}