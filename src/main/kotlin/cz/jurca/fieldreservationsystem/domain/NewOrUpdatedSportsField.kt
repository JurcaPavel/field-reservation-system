package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either

class NewOrUpdatedSportsField(
    val name: Name,
    val coordinates: Coordinates,
    val address: Address,
    val description: Description?,
    val sportTypes: List<SportType>,
    private val createSportsFieldProvider: suspend (NewOrUpdatedSportsField) -> SportsField,
    private val updateSportsFieldProvider: suspend (NewOrUpdatedSportsField) -> SportsField,
) {
    suspend fun create(): Either<NotManagerOrAdminError, SportsField> {
        // TODO only field manager or admin can create sports field
        return either {
//            ensure()
            createSportsFieldProvider(this@NewOrUpdatedSportsField)
        }
    }

    suspend fun update(id: SportsFieldId): Either<NotManagerOrAdminError, SportsField> {
        // TODO check if resource exists
        // TODO only manager that created the field or admin can update the field
        return either {
            updateSportsFieldProvider(this@NewOrUpdatedSportsField)
        }
    }
}