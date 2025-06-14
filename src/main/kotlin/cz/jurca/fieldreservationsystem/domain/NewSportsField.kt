package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.NotManagerOrAdminError

class NewSportsField(
    val name: Name,
    val coordinates: Coordinates,
    val address: Address,
    val description: Description?,
    val sportTypes: List<SportType>,
    val loginUser: LoginUser,
    private val createSportsFieldProvider: suspend (NewSportsField) -> SportsField,
) {
    suspend fun create(): Either<NotManagerOrAdminError, SportsField> {
        return either {
            ensure(loginUser.isManager() || loginUser.isAdmin()) {
                NotManagerOrAdminError("Only field manager or admin can create sports field")
            }
            createSportsFieldProvider(this@NewSportsField)
        }
    }
}