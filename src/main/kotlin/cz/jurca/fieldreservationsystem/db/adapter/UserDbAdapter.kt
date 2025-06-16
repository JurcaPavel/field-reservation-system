package cz.jurca.fieldreservationsystem.db.adapter

import cz.jurca.fieldreservationsystem.db.repository.UserDao
import cz.jurca.fieldreservationsystem.db.repository.UserDaoId
import cz.jurca.fieldreservationsystem.db.repository.UserRepository
import cz.jurca.fieldreservationsystem.domain.Email
import cz.jurca.fieldreservationsystem.domain.NewUserRegistration
import cz.jurca.fieldreservationsystem.domain.UnvalidatedUserId
import cz.jurca.fieldreservationsystem.domain.User
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.domain.Username
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserDbAdapter(private val userRepository: UserRepository) {
    internal fun getId(userDaoId: UserDaoId): UserId = UserId(userDaoId.value, this::getDetail)

    suspend fun findUser(id: UnvalidatedUserId): UserId? = userRepository.findById(id.value)?.run { UserId(getDaoId().value, this@UserDbAdapter::getDetail) }

    suspend fun getDetail(id: UserId): User = requireNotNull(userRepository.findById(id.value)).toDomain(id)

    @Transactional
    suspend fun create(newUserRegistration: NewUserRegistration): User =
        userRepository.save(
            UserDao(
                name = newUserRegistration.name.value,
                username = newUserRegistration.username.value,
                email = newUserRegistration.email.value,
                password = newUserRegistration.password.value,
                role = newUserRegistration.role.name,
            ),
        ).let { dao -> dao.toDomain(getId(dao.getDaoId())) }

    suspend fun existsByUsername(username: Username): Boolean = userRepository.existsByUsername(username.value)

    suspend fun existsByEmail(email: Email): Boolean = userRepository.existsByEmail(email.value)
}