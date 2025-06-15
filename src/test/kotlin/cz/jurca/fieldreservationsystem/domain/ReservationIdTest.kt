package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock

class ReservationIdTest : BaseTest() {
    @Test
    fun `given user is admin when get detail then return reservation`() =
        runBlocking {
            Given()
            val reservation = mock<Reservation>()
            val loginUser =
                LoginUser(
                    id = mock(),
                    username = Username("admin"),
                    role = UserRole.ADMIN,
                    isSportsFieldOwnerProvider = { _, _ -> false },
                )
            val reservationId = ReservationId(1) { reservation }

            When()
            val result = reservationId.getDetail(loginUser)

            Then()
            result shouldBeRight reservation
        }

    @Test
    fun `given user is owner of reservation when get detail then return reservation`() =
        runBlocking {
            Given()
            val userId = mock<UserId>()
            val reservation = Reservation(mock(), userId, mock(), mock(), mock(), mock())
            val loginUser =
                LoginUser(
                    id = userId,
                    username = Username("owner"),
                    role = UserRole.BASIC,
                    isSportsFieldOwnerProvider = { _, _ -> false },
                )
            val reservationId = ReservationId(1) { reservation }

            When()
            val result = reservationId.getDetail(loginUser)

            Then()
            result shouldBeRight reservation
        }

    @Test
    fun `given user is sports field owner when get detail then return reservation`() =
        runBlocking {
            Given()
            val reservation = Reservation(mock(), mock(), mock(), mock(), mock(), mock())
            val loginUser =
                LoginUser(
                    id = mock(),
                    username = Username("fieldOwner"),
                    role = UserRole.BASIC,
                    isSportsFieldOwnerProvider = { _, _ -> true },
                )
            val reservationId = ReservationId(1) { reservation }

            When()
            val result = reservationId.getDetail(loginUser)

            Then()
            result shouldBeRight reservation
        }

    @Test
    fun `given user is not admin, reservation owner or sports field owner when get detail then return NotResourceOwnerError`() =
        runBlocking {
            Given()
            val userId = UserId(1) { mock() }
            val otherUserId = UserId(2) { mock() }
            val reservation = Reservation(mock(), userId, mock(), mock(), mock(), mock())
            val loginUser =
                LoginUser(
                    id = otherUserId,
                    username = Username("otherUser"),
                    role = UserRole.BASIC,
                    isSportsFieldOwnerProvider = { _, _ -> false },
                )
            val reservationId = ReservationId(1) { reservation }

            When()
            val result = reservationId.getDetail(loginUser)

            Then()
            result.shouldBeLeft().run {
                message shouldBe "User otherUser is not an owner of the reservation with id 1"
            }
        }
}