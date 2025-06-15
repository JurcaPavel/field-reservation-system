package cz.jurca.fieldreservationsystem.domain

class Reservation(
    val id: ReservationId,
    val ownerId: UserId,
    val sportsFieldId: SportsFieldId,
    val timeSlot: TimeSlot,
    val userNote: Note?,
    val ownerNote: Note?,
)