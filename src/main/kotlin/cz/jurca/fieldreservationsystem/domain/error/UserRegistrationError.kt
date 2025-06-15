package cz.jurca.fieldreservationsystem.domain.error

sealed class UserRegistrationError(open val message: String)

data class RegisterAsAdminError(override val message: String) : UserRegistrationError(message)

data class UsernameAlreadyExistsError(override val message: String) : UserRegistrationError(message)

data class EmailAlreadyExistsError(override val message: String) : UserRegistrationError(message)