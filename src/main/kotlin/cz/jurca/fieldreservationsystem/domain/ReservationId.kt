package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError

data class ReservationId(
    val value: Int,
    private val detailProvider: suspend (ReservationId) -> Reservation,
) : ReservationValidationResult() {
    suspend fun getDetail(loginUser: LoginUser): Either<NotResourceOwnerError, Reservation> =
        either {
            val reservation = detailProvider(this@ReservationId)
            ensure(
                loginUser.isAdmin()
                    || loginUser.isOwnerOfReservation(reservation)
                    || loginUser.isSportsFieldOwner(reservation.sportsFieldId)
            ) { NotResourceOwnerError("User ${loginUser.username.value} is not an owner of the reservation with id $value") }
            reservation
        }
}

data class UnvalidatedReservationId(val value: Int) {
    suspend fun validate(validateReservation: suspend (UnvalidatedReservationId) -> ReservationId?): Result<ReservationId> =
        runCatching {
            validateReservation(this).let { reservationId ->
                reservationId ?: throw IllegalArgumentException("Reservation with ID $value does not exist.")
            }
        }
}

sealed class ReservationValidationResult

data object ReservationNotFound : ReservationValidationResult()