package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

class Email private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): Either<EmailValidationError, Email> =
            either {
                ensure(value.isNotBlank()) { EmailValidationError.EmptyEmailName }
                ensure(isValidEmailFormat(value)) { EmailValidationError.InvalidEmailFormat }
                Email(value)
            }

        private fun isValidEmailFormat(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9+_][A-Za-z0-9+_.-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$".toRegex()
            return email.matches(emailRegex)
        }
    }
}

sealed class EmailValidationError(val message: String) {
    data object EmptyEmailName : EmailValidationError("Email cannot be empty.")

    data object InvalidEmailFormat : EmailValidationError("Invalid email format.")
}