package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter
import org.springframework.stereotype.Component

@Component
class IdValidator(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
) {
    suspend fun existsSportsField(id: Int): SportsFieldValidationResult = UnvalidatedSportsFieldId(id).validate(sportsFieldDbAdapter::findSportsField).getOrElse { SportFieldNotFound }
}