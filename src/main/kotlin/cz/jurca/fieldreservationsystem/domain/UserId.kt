package cz.jurca.fieldreservationsystem.domain

data class UserId(
    val value: Int,
    private val detailProvider: suspend (UserId) -> User,
) : UserValidationResult() {
    suspend fun getDetail(): User = detailProvider(this)
}

data class UnvalidatedUserId(val value: Int) {
    suspend fun validate(validateUser: suspend (UnvalidatedUserId) -> UserId?): Result<UserId> =
        runCatching {
            validateUser(this).let { userId ->
                userId ?: throw IllegalArgumentException("User with ID $value does not exist")
            }
        }
}

sealed class UserValidationResult

data object UserNotFound : UserValidationResult()