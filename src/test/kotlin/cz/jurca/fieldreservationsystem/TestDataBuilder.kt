package cz.jurca.fieldreservationsystem

import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.repository.SportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import org.springframework.stereotype.Component

@Component
class TestDataBuilder(
    private val repository: TestRepository,
) {
    fun deleteAll() = repository.deleteAll()

    fun buildInitialData() {
        buildDefaultSportTypes()
    }

    fun buildSportsField(
        name: String = "Hřiště na Karlově mostě",
        city: String = "Prague",
        street: String = "Karlův most (street)",
        zipCode: String = "16400",
        countryCode: String = "CZE",
        latitude: Double = 50.086401,
        longitude: Double = 14.412209,
        description: String? = "Skvělé hřiště uprostřed Karlova mostu!",
        sportTypes: List<SportType> = listOf(SportType.SOCCER, SportType.BASKETBALL),
    ): SportsFieldDao = repository.saveSportsField(
        SportsFieldDao(
            name = name,
            city = city,
            street = street,
            zipCode = zipCode,
            countryCode = countryCode,
            latitude = latitude,
            longitude = longitude,
            description = description
        )
    ).let {
        sportTypes.forEach { sportType ->
            repository.saveSportsFieldSportsType(
                SportsFieldSportTypeDao(
                    sportsFieldId = it.getDaoId(),
                    sportTypeId = repository.findSportTypeByName(sportType.name).getDaoId()
                )
            )
        }
        it
    }

    fun buildSportsFieldSportsType(
        sportsFieldId: Int,
        sportTypeId: Int,
    ): SportsFieldSportTypeDao = repository.saveSportsFieldSportsType(
        SportsFieldSportTypeDao(
            sportsFieldId = sportsFieldId,
            sportTypeId = sportTypeId
        )
    )

    fun buildSportType(name: String): SportTypeDao = repository.saveSportType(name)

    private fun buildDefaultSportTypes() {
        SportType.entries.forEach { sportType ->
            buildSportType(sportType.name)
        }
    }
}