package cz.jurca.fieldreservationsystem

import cz.jurca.fieldreservationsystem.db.repository.ReservationDao
import cz.jurca.fieldreservationsystem.db.repository.SportTypeDao
import cz.jurca.fieldreservationsystem.db.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.db.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.db.repository.UserDao
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.UserRole
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
        defaultManager = buildUser(username = "pjm", email = "manager@email.com", role = UserRole.MANAGER)
    }

    fun buildUser(
        name: String = "Pavel Jurča",
        username: String = "pja",
        email: String = "admin@email.com",
        password: String = "userpassword",
        role: UserRole = UserRole.ADMIN,
    ): UserDao =
        repository.saveUser(
            UserDao(
                name = name,
                username = username,
                email = email,
                password = passwordEncoder.encode(password),
                role = role.name,
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
        managerId: Int = defaultManager.getDaoId().value,
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
                managerId = managerId,
            ),
        ).let {
            sportTypes.forEach { sportType ->
                repository.saveSportsFieldSportsType(
                    SportsFieldSportTypeDao(
                        sportsFieldId = it.getDaoId().value,
                        sportTypeId = repository.findSportTypeByName(sportType.name).getDaoId().value,
                    ),
                )
            }
            it
        }

    fun buildReservation(
        ownerId: Int,
        sportsFieldId: Int,
        startTime: OffsetDateTime = OffsetDateTime.of(2100, 6, 28, 10, 0, 0, 0, ZoneOffset.UTC),
        endTime: OffsetDateTime = OffsetDateTime.of(2100, 6, 28, 12, 0, 0, 0, ZoneOffset.UTC),
        userNote: String? = "User note for reservation",
        fieldManagerNote: String? = "Field manager note for reservation",
    ): ReservationDao =
        repository.saveReservation(
            ReservationDao(
                ownerId = ownerId,
                sportsFieldId = sportsFieldId,
                startTime = startTime,
                endTime = endTime,
                userNote = userNote,
                fieldManagerNote = fieldManagerNote,
            ),
        )

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