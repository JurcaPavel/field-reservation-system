package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.domain.UnvalidatedUserId
import cz.jurca.fieldreservationsystem.domain.User
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserDbAdapter(private val userRepository: UserRepository) {
    suspend fun findUser(id: UnvalidatedUserId): UserId? = userRepository.findById(id.value)?.run { UserId(getDaoId(), this@UserDbAdapter::getDetail) }

    suspend fun getDetail(id: UserId): User = requireNotNull(userRepository.findById(id.value)).toDomain(id)
}