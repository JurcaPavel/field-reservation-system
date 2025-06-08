package cz.jurca.fieldreservationsystem.domain

sealed class User(
    val id: UserId,
    val name: Name,
    val username: Username,
    val email: Email,
    val role: UserRole,
)

class BasicUser(
    id: UserId,
    name: Name,
    username: Username,
    email: Email,
) : User(id, name, username, email, UserRole.BASIC)

class ManagerUser(
    id: UserId,
    name: Name,
    username: Username,
    email: Email,
) : User(id, name, username, email, UserRole.MANAGER)

class AdminUser(
    id: UserId,
    name: Name,
    username: Username,
    email: Email,
) : User(id, name, username, email, UserRole.ADMIN)

enum class UserRole {
    BASIC,
    MANAGER,
    ADMIN,
    ;

    companion object {
        fun fromSpringRole(authority: String): UserRole =
            when (authority) {
                "BASIC" -> BASIC
                "MANAGER" -> MANAGER
                "ADMIN" -> ADMIN
                else -> throw IllegalArgumentException("Unknown role: $authority")
            }
    }
}