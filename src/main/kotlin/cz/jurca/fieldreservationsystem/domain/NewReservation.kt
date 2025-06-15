package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.AlreadyReservedError

class NewReservation(
    val ownerId: UserId,
    val sportsFieldId: SportsFieldId,
    val timeSlot: TimeSlot,
    val userNote: Note? = null,
    val fieldManagerNote: Note? = null,
    private val createReservationProvider: suspend (NewReservation) -> Reservation,
    private val isAlreadyReserved: suspend (SportsFieldId, TimeSlot) -> Boolean,
) {
    suspend fun create(): Either<AlreadyReservedError, Reservation> =
        either {
            ensure(
                !isAlreadyReserved(sportsFieldId, timeSlot),
            ) { AlreadyReservedError("The timeslot ${timeSlot.startTime.toDateTimeString()}-${timeSlot.endTime.toDateTimeString()} is already reserved for sports field with id ${sportsFieldId.value}") }
            createReservationProvider(this@NewReservation)
        }
}