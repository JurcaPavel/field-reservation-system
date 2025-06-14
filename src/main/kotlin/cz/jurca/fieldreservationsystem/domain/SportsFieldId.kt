package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError
import cz.jurca.fieldreservationsystem.domain.error.SportsFieldDeletionError

data class SportsFieldId(
    val value: Int,
    private val detailProvider: suspend (SportsFieldId) -> SportsField,
    private val deletionProvider: suspend (SportsFieldId) -> Unit,
) : SportsFieldValidationResult() {
    suspend fun getDetail(): SportsField = detailProvider(this)

    suspend fun delete(loginUser: LoginUser): Either<SportsFieldDeletionError, Success> {
        return either {
            ensure(loginUser.isManager() || loginUser.isAdmin()) { NotManagerOrAdminError("User ${loginUser.username.value} cannot delete sports field  with id $value because he is not a manager or admin.") }
            ensure(loginUser.isSportsFieldOwner(this@SportsFieldId) || loginUser.isAdmin()) { NotResourceOwnerError("User ${loginUser.username.value} cannot delete sports field  with id $value because he is not the owner of the sports field.") }
            deletionProvider(this@SportsFieldId).let { Success }
        }
    }
}

data class UnvalidatedSportsFieldId(val value: Int) {
    suspend fun validate(validateSportsField: suspend (UnvalidatedSportsFieldId) -> SportsFieldId?): Result<SportsFieldId> =
        runCatching {
            validateSportsField(this).let { sportsFieldId ->
                sportsFieldId ?: throw IllegalArgumentException("Sports field with ID $value does not exist.")
            }
        }
}

sealed class SportsFieldValidationResult

data object SportFieldNotFound : SportsFieldValidationResult()