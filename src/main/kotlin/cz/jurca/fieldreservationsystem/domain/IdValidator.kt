package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter
import cz.jurca.fieldreservationsystem.repository.adapter.UserDbAdapter
import org.springframework.stereotype.Component

@Component
class IdValidator(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userDbAdapter: UserDbAdapter,
) {
    suspend fun existsSportsField(id: Int): SportsFieldValidationResult = UnvalidatedSportsFieldId(id).validate(sportsFieldDbAdapter::findSportsField).getOrElse { SportFieldNotFound }

    suspend fun existsUser(id: Int): UserValidationResult = UnvalidatedUserId(id).validate(userDbAdapter::findUser).getOrElse { UserNotFound }
}