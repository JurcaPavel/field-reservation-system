package cz.jurca.fieldreservationsystem.domain

class LoginUser(
    val id: UserId,
    val username: Username,
    val role: UserRole,
    private val isSportsFieldOwnerProvider: suspend (UserId, SportsFieldId) -> Boolean,
) {
    fun isAdmin(): Boolean = role == UserRole.ADMIN

    fun isManager(): Boolean = role == UserRole.MANAGER

    fun isOwnerOfReservation(reservation: Reservation): Boolean = reservation.ownerId.value == id.value

    suspend fun isSportsFieldOwner(sportsFieldId: SportsFieldId): Boolean = isSportsFieldOwnerProvider(id, sportsFieldId)
}