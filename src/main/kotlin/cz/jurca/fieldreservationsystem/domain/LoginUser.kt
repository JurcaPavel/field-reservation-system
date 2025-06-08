package cz.jurca.fieldreservationsystem.domain

class LoginUser(
    val id: Int,
    val username: Username,
    val role: UserRole,
) {
    fun isAdmin(): Boolean = role == UserRole.ADMIN

    fun isManager(): Boolean = role == UserRole.MANAGER
}