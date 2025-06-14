package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError

class UpdatedSportsField(
    val id: SportsFieldId,
    val name: Name,
    val coordinates: Coordinates,
    val address: Address,
    val description: Description?,
    val sportTypes: List<SportType>,
    val loginUser: LoginUser,
    private val updateSportsFieldProvider: suspend (UpdatedSportsField) -> SportsField,
) {
    suspend fun update(): Either<NotResourceOwnerError, SportsField> =
        either {
            ensure(loginUser.isAdmin() || loginUser.id.value == id.getDetail().managerId.value) { NotResourceOwnerError("User ${loginUser.username.value} cannot edit sports field with id ${id.value} because he is not manager of this field.") }
            updateSportsFieldProvider(this@UpdatedSportsField)
        }
}