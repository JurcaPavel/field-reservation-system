package cz.jurca.fieldreservationsystem

import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.repository.SportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.repository.UserDao
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class TestDataBuilder(
    private val passwordEncoder: PasswordEncoder,
    private val repository: TestRepository,
) {
    fun deleteAll() = repository.deleteAll()

    lateinit var defaultAdmin: UserDao
    lateinit var defaultManager: UserDao

    fun buildInitialData() {
        buildDefaultSportTypes()
        defaultAdmin = buildUser()
        defaultManager = buildUser(username = "pjm", email = "manager@email.com", role = "MANAGER")
    }

    fun buildUser(
        name: String = "Pavel Jurča",
        username: String = "pja",
        email: String = "admin@email.com",
        password: String = "userpassword",
        role: String = "ADMIN",
    ): UserDao =
        repository.saveUser(
            UserDao(
                name = name,
                username = username,
                email = email,
                password = passwordEncoder.encode(password),
                role = role,
            ),
        )

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
    ): SportsFieldDao =
        repository.saveSportsField(
            SportsFieldDao(
                name = name,
                city = city,
                street = street,
                zipCode = zipCode,
                countryCode = countryCode,
                latitude = latitude,
                longitude = longitude,
                description = description,
                managerId = defaultManager.getDaoId(),
            ),
        ).let {
            sportTypes.forEach { sportType ->
                repository.saveSportsFieldSportsType(
                    SportsFieldSportTypeDao(
                        sportsFieldId = it.getDaoId(),
                        sportTypeId = repository.findSportTypeByName(sportType.name).getDaoId(),
                    ),
                )
            }
            it
        }

    fun buildSportsFieldSportsType(
        sportsFieldId: Int,
        sportTypeId: Int,
    ): SportsFieldSportTypeDao =
        repository.saveSportsFieldSportsType(
            SportsFieldSportTypeDao(
                sportsFieldId = sportsFieldId,
                sportTypeId = sportTypeId,
            ),
        )

    fun buildSportType(name: String): SportTypeDao = repository.saveSportType(name)

    private fun buildDefaultSportTypes() {
        SportType.entries.forEach { sportType ->
            buildSportType(sportType.name)
        }
    }
}