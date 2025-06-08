package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either

class UpdatedSportsField(
    val id: SportsFieldId,
    val name: Name,
    val coordinates: Coordinates,
    val address: Address,
    val description: Description?,
    val sportTypes: List<SportType>,
    private val updateSportsFieldProvider: suspend (UpdatedSportsField) -> SportsField,
) {
    suspend fun update(id: SportsFieldId): Either<NotManagerOrAdminError, SportsField> {
        // TODO check if resource exists
        // TODO only manager that created the field or admin can update the field
        return either {
            updateSportsFieldProvider(this@UpdatedSportsField)
        }
    }
}