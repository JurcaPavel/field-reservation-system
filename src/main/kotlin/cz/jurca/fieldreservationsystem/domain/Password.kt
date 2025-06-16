package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

@JvmInline
value class NewPassword private constructor(val value: String) {
    companion object {
        private const val MINIMAL_PASSWORD_LENGTH = 8

        operator fun invoke(value: String): Either<PasswordValidationError, NewPassword> =
            either {
                ensure(value.length >= MINIMAL_PASSWORD_LENGTH) { PasswordValidationError.TooShort }
                ensure(value.any { it.isDigit() }) { PasswordValidationError.NoDigit }
                ensure(value.any { it.isUpperCase() }) { PasswordValidationError.NoUppercase }
                ensure(value.any { it.isLowerCase() }) { PasswordValidationError.NoLowercase }
                ensure(value.any { !it.isLetterOrDigit() }) { PasswordValidationError.NoSpecialChar }
                NewPassword(value)
            }
    }

    fun encode(encodeProvider: (rawPassword: CharSequence) -> String): EncodedPassword = EncodedPassword(encodeProvider(value))
}

@JvmInline
value class EncodedPassword(val value: String)

@JvmInline
value class AuthenticationPassword(val value: String)

sealed class PasswordValidationError(val message: String) {
    data object TooShort : PasswordValidationError("Password is too short, it has to be at least 8 characters long")

    data object NoDigit : PasswordValidationError("Password must contain at least one digit")

    data object NoUppercase : PasswordValidationError("Password must contain at least one uppercase letter")

    data object NoLowercase : PasswordValidationError("Password must contain at least one lowercase letter")

    data object NoSpecialChar : PasswordValidationError("Password must contain at least one special character")
}