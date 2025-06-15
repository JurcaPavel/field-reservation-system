package cz.jurca.fieldreservationsystem.api.reservation.mutation

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.AlreadyReservedError
import cz.jurca.fieldreservationsystem.codegen.types.CreateReservationInput
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.Reservation
import cz.jurca.fieldreservationsystem.codegen.types.TimeSlotInput
import cz.jurca.fieldreservationsystem.domain.UserRole
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CreateReservationMutationIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given everything ok when create reservation then reservation is saved correctly in db and returned`() =
        runBlocking {
            Given()
            val basicUserDao = dataBuilder.buildUser(username = "pjb", email = "basic@email.com", role = UserRole.BASIC)
            setUserInTestSecurityContextHolder(basicUserDao)
            val sportsField = dataBuilder.buildSportsField()
            val input =
                CreateReservationInput(
                    sportsFieldId = sportsField.getDaoId().value.toString(),
                    timeslot =
                        TimeSlotInput(
                            startTime = "2100-06-28T10:00:00Z",
                            endTime = "2100-06-28T12:00:00Z",
                        ),
                    userNote = "Test user note",
                    fieldManagerNote = "Test field manager note",
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    createReservationMutationRequest(input),
                    "data.createReservation",
                    Reservation::class.java,
                )

            Then()
            response.run {
                id shouldNotBe null
                userId shouldBe basicUserDao.getDaoId().value.toString()
                sportsFieldId shouldBe sportsField.getDaoId().value.toString()
                timeslot.startTime shouldBe "2100-06-28T10:00:00Z"
                timeslot.endTime shouldBe "2100-06-28T12:00:00Z"
                userNote shouldBe "Test user note"
                fieldManagerNote shouldBe "Test field manager note"
            }
            repository.findAllReservations().shouldHaveSize(1).first().run {
                getDaoId().value shouldBe response.id.toInt()
                ownerId shouldBe basicUserDao.getDaoId().value
                sportsFieldId shouldBe sportsField.getDaoId().value
                startTime.toInstant().toString() shouldBe "2100-06-28T10:00:00Z"
                endTime.toInstant().toString() shouldBe "2100-06-28T12:00:00Z"
                userNote shouldBe "Test user note"
                fieldManagerNote shouldBe "Test field manager note"
            }
        }

    @Test
    fun `given sportsfield not found when create reservation then return NotFoundError`() =
        runBlocking {
            Given()
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)
            val input =
                CreateReservationInput(
                    sportsFieldId = "666",
                    timeslot =
                        TimeSlotInput(
                            startTime = "2100-06-28T10:00:00Z",
                            endTime = "2100-06-28T12:00:00Z",
                        ),
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    createReservationMutationRequest(input),
                    "data.createReservation",
                    NotFoundError::class.java,
                )

            Then()
            response.message shouldBe "Sports field with id 666 not found. "
            repository.findAllReservations().size shouldBe 0
        }

    @Test
    fun `given already reserved timeslot when create reservation then return AlreadyReservedError`() =
        runBlocking {
            Given()
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)
            val sportsFieldDao = dataBuilder.buildSportsField()
            val startTime = OffsetDateTime.of(2100, 6, 28, 10, 0, 0, 0, ZoneOffset.UTC)
            val endTime = OffsetDateTime.of(2100, 6, 28, 12, 0, 0, 0, ZoneOffset.UTC)

            dataBuilder.buildReservation(
                ownerId = dataBuilder.defaultAdmin.getDaoId().value,
                sportsFieldId = sportsFieldDao.getDaoId().value,
                startTime = startTime,
                endTime = endTime,
            )

            val input =
                CreateReservationInput(
                    sportsFieldId = sportsFieldDao.getDaoId().value.toString(),
                    timeslot =
                        TimeSlotInput(
                            startTime = "2100-06-28T10:00:00Z",
                            endTime = "2100-06-28T12:00:00Z",
                        ),
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    createReservationMutationRequest(input),
                    "data.createReservation",
                    AlreadyReservedError::class.java,
                )

            Then()
            response.message shouldBe "The timeslot 2100-06-28T10:00:00Z-2100-06-28T12:00:00Z is already reserved for sports field with id ${sportsFieldDao.getDaoId().value}"
        }

    private val createReservationMutationRequest: (createReservationInput: CreateReservationInput) -> String = { input ->
        DgsClient.buildMutation {
            createReservation(input) {
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
                onAlreadyReservedError {
                    message
                }
                onNotFoundError {
                    message
                }
            }
        }
    }
}