package cz.jurca.fieldreservationsystem.domain

data class SportsFieldId(
    val value: Int,
    private val detailProvider: suspend (SportsFieldId) -> SportsField,
) : SportsFieldValidationResult() {
    suspend fun getDetail(): SportsField = detailProvider(this)
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