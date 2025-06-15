package cz.jurca.fieldreservationsystem.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import cz.jurca.fieldreservationsystem.domain.error.EmailAlreadyExistsError
import cz.jurca.fieldreservationsystem.domain.error.RegisterAsAdminError
import cz.jurca.fieldreservationsystem.domain.error.UserRegistrationError
import cz.jurca.fieldreservationsystem.domain.error.UsernameAlreadyExistsError

class NewUserRegistration(
    val name: Name,
    val username: Username,
    val email: Email,
    val password: EncodedPassword,
    val role: UserRole,
    private val registrationProvider: suspend (NewUserRegistration) -> User,
    private val usernameExistsProvider: suspend (Username) -> Boolean,
    private val emailExistsProvider: suspend (Email) -> Boolean,
) {
    suspend fun register(): Either<UserRegistrationError, User> =
        either {
            ensure(role != UserRole.ADMIN) { RegisterAsAdminError("Cannot register as admin!") }
            ensure(!usernameExistsProvider(username)) { UsernameAlreadyExistsError("User with username [${username.value}] already exists!") }
            ensure(!emailExistsProvider(email)) { EmailAlreadyExistsError("User with email [${email.value}] already exists!") }
            registrationProvider(this@NewUserRegistration)
        }
}