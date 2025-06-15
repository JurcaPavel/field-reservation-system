package cz.jurca.fieldreservationsystem.repository

import cz.jurca.fieldreservationsystem.domain.AdminUser
import cz.jurca.fieldreservationsystem.domain.BasicUser
import cz.jurca.fieldreservationsystem.domain.Email
import cz.jurca.fieldreservationsystem.domain.ManagerUser
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.User
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.domain.Username
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : CoroutineCrudRepository<UserDao, Int> {
    fun findByUsername(username: String): Mono<UserDao?>
}

@Table("app_user")
class UserDao(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): UserDaoId = UserDaoId(id!!)

    fun toDomain(userId: UserId): User =
        when (role) {
            "ADMIN" ->
                AdminUser(
                    id = userId,
                    name = Name(name),
                    username = Username(username),
                    email = requireNotNull(Email(email).getOrNull()) { "Email in db should be valid for user $email" },
                )

            "MANAGER" ->
                ManagerUser(
                    id = userId,
                    name = Name(name),
                    username = Username(username),
                    email = requireNotNull(Email(email).getOrNull()) { "Email in db should be valid for user $email" },
                )

            "BASIC" ->
                BasicUser(
                    id = userId,
                    name = Name(name),
                    username = Username(username),
                    email = requireNotNull(Email(email).getOrNull()) { "Email in db should be valid for user $email" },
                )

            else -> throw IllegalArgumentException("Unknown role: $role")
        }
}

data class UserDaoId(val value: Int)