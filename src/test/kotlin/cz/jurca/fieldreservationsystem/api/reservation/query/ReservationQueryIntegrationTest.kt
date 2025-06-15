package cz.jurca.fieldreservationsystem.api.reservation.query

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.Reservation
import cz.jurca.fieldreservationsystem.domain.UserRole
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import java.time.ZoneOffset

class ReservationQueryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given a reservation in db when get by id then return the correct one`() =
        runBlocking {
            Given()
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)
            val sportsFieldDao = dataBuilder.buildSportsField()

            val userDao =
                dataBuilder.buildUser(
                    name = "Basic User",
                    username = "basicuser",
                    email = "basic@example.com",
                    role = UserRole.BASIC,
                )

            val reservationDao =
                dataBuilder.buildReservation(
                    ownerId = userDao.getDaoId().value,
                    sportsFieldId = sportsFieldDao.getDaoId().value,
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    reservationQueryRequest(reservationDao.getDaoId().value),
                    "data.reservation",
                    Reservation::class.java,
                )

            Then()
            response.run {
                id shouldBe reservationDao.getDaoId().value.toString()
                userId shouldBe userDao.getDaoId().value.toString()
                sportsFieldId shouldBe sportsFieldDao.getDaoId().value.toString()
                OffsetDateTime.parse(timeslot.startTime).toInstant() shouldBe OffsetDateTime.of(2100, 6, 28, 10, 0, 0, 0, ZoneOffset.UTC).toInstant()
                OffsetDateTime.parse(timeslot.endTime).toInstant() shouldBe OffsetDateTime.of(2100, 6, 28, 12, 0, 0, 0, ZoneOffset.UTC).toInstant()
                userNote shouldBe "User note for reservation"
                fieldManagerNote shouldBe "Field manager note for reservation"
            }
        }

    @Test
    fun `given reservations in db when get by nonexisting id then return error`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()

            val userDao =
                dataBuilder.buildUser(
                    name = "Basic User",
                    username = "basicuser",
                    email = "basic@example.com",
                    role = UserRole.BASIC,
                )

            dataBuilder.buildReservation(
                ownerId = userDao.getDaoId().value,
                sportsFieldId = sportsFieldDao.getDaoId().value,
            )

            When()
            val result =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    reservationQueryRequest(999999),
                    "data.reservation",
                    NotFoundError::class.java,
                )

            Then()
            result.message shouldBe "Reservation with id 999999 not found"
        }

    private val reservationQueryRequest: (id: Int) -> String = { requestedId ->
        DgsClient.buildQuery {
            reservation(requestedId.toString()) {
                onReservation {
                    id
                    userId
                    sportsFieldId
                    timeslot {
                        startTime
                        endTime
                    }
                    userNote
                    fieldManagerNote
                }
                onNotFoundError { message }
            }
        }
    }
}