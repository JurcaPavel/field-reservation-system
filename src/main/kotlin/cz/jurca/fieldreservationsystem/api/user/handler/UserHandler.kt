package cz.jurca.fieldreservationsystem.api.user.handler

import cz.jurca.fieldreservationsystem.db.adapter.UserDbAdapter
import cz.jurca.fieldreservationsystem.domain.AuthResult.Failure
import cz.jurca.fieldreservationsystem.domain.AuthResult.Success
import cz.jurca.fieldreservationsystem.domain.AuthenticationPassword
import cz.jurca.fieldreservationsystem.domain.BasicAuthentication
import cz.jurca.fieldreservationsystem.domain.Email
import cz.jurca.fieldreservationsystem.domain.EmailValidationError
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NewPassword
import cz.jurca.fieldreservationsystem.domain.NewUserRegistration
import cz.jurca.fieldreservationsystem.domain.UserRole
import cz.jurca.fieldreservationsystem.domain.Username
import cz.jurca.fieldreservationsystem.domain.error.EmailAlreadyExistsError
import cz.jurca.fieldreservationsystem.domain.error.RegisterAsAdminError
import cz.jurca.fieldreservationsystem.domain.error.UsernameAlreadyExistsError
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.net.URI

@Component
class UserHandler(
    private val userDetailsService: ReactiveUserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    private val userDbAdapter: UserDbAdapter,
) {
    suspend fun authenticate(request: ServerRequest): ServerResponse =
        request.awaitBody<BasicAuthInput>().let { basicAuthInput ->
            BasicAuthentication(
                Username(basicAuthInput.username),
                AuthenticationPassword(basicAuthInput.password),
                userDetailsService::findByUsername,
                passwordEncoder::matches,
            ).authenticate().let { authResult ->
                when (authResult) {
                    is Success -> {
                        val headers =
                            HttpHeaders().apply {
                                setBasicAuth(authResult.username.value, basicAuthInput.password)
                            }
                        ServerResponse.ok().headers { it.addAll(headers) }.bodyValueAndAwait(BasicAuthOutput(authResult.status.value))
                    }

                    is Failure -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(BasicAuthOutput(authResult.reason.value))
                }
            }
        }

    // TODO refactor later
    suspend fun register(request: ServerRequest): ServerResponse =
        request.awaitBody<RegistrationInput>().let { registrationInput ->
            NewUserRegistration(
                name = Name(registrationInput.name),
                username = Username(registrationInput.username),
                email =
                    Email(registrationInput.email).fold(
                        ifLeft = { validationError ->
                            when (validationError) {
                                is EmailValidationError.EmptyEmailName -> return ServerResponse.badRequest().bodyValueAndAwait(RegistrationOutput("Email cannot be empty."))
                                is EmailValidationError.InvalidEmailFormat -> return ServerResponse.badRequest().bodyValueAndAwait(RegistrationOutput("Invalid email format."))
                            }
                        },
                        ifRight = { email -> email },
                    ),
                password =
                    NewPassword(registrationInput.password).fold(
                        ifLeft = { validationError -> return ServerResponse.badRequest().bodyValueAndAwait(RegistrationOutput(validationError.message)) },
                        ifRight = { rawPassword -> rawPassword.encode(passwordEncoder::encode) },
                    ),
                role = UserRole.valueOf(registrationInput.role.uppercase()),
                registrationProvider = userDbAdapter::create,
                usernameExistsProvider = userDbAdapter::existsByUsername,
                emailExistsProvider = userDbAdapter::existsByEmail,
            ).register().let { registrationResult ->
                registrationResult.fold(
                    ifLeft = { registrationError ->
                        when (registrationError) {
                            is RegisterAsAdminError -> ServerResponse.badRequest().bodyValueAndAwait(RegistrationOutput(registrationError.message))
                            is EmailAlreadyExistsError -> ServerResponse.status(HttpStatus.CONFLICT).bodyValueAndAwait(RegistrationOutput(registrationError.message))
                            is UsernameAlreadyExistsError -> ServerResponse.status(HttpStatus.CONFLICT).bodyValueAndAwait(RegistrationOutput(registrationError.message))
                        }
                    },
                    ifRight = { registeredUser -> ServerResponse.created(URI.create("/")).bodyValueAndAwait(RegistrationOutput("User [${registeredUser.username.value}] registered successfully.")) },
                )
            }
        }

    data class BasicAuthInput(
        val username: String,
        val password: String,
    )

    data class BasicAuthOutput(
        val status: String,
    )

    data class RegistrationInput(
        val name: String,
        val username: String,
        val email: String,
        val password: String,
        val role: String,
    )

    data class RegistrationOutput(
        val message: String,
    )
}