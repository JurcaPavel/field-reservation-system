package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.domain.UnvalidatedUserId
import cz.jurca.fieldreservationsystem.domain.User
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.repository.UserDaoId
import cz.jurca.fieldreservationsystem.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserDbAdapter(private val userRepository: UserRepository) {
    internal fun getId(userDaoId: UserDaoId): UserId = UserId(userDaoId.value, this::getDetail)

    suspend fun findUser(id: UnvalidatedUserId): UserId? = userRepository.findById(id.value)?.run { UserId(getDaoId().value, this@UserDbAdapter::getDetail) }

    suspend fun getDetail(id: UserId): User = requireNotNull(userRepository.findById(id.value)).toDomain(id)
}