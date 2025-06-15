package cz.jurca.fieldreservationsystem.api.user.handler

import arrow.core.Either
import cz.jurca.fieldreservationsystem.domain.AuthResult.Failure
import cz.jurca.fieldreservationsystem.domain.AuthResult.Success
import cz.jurca.fieldreservationsystem.domain.BasicAuthentication
import cz.jurca.fieldreservationsystem.domain.Email
import cz.jurca.fieldreservationsystem.domain.EmptyEmailName
import cz.jurca.fieldreservationsystem.domain.EncodedPassword
import cz.jurca.fieldreservationsystem.domain.InvalidEmailFormat
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NewUserRegistration
import cz.jurca.fieldreservationsystem.domain.RawPassword
import cz.jurca.fieldreservationsystem.domain.UserRole
import cz.jurca.fieldreservationsystem.domain.Username
import cz.jurca.fieldreservationsystem.domain.error.EmailAlreadyExistsError
import cz.jurca.fieldreservationsystem.domain.error.RegisterAsAdminError
import cz.jurca.fieldreservationsystem.domain.error.UsernameAlreadyExistsError
import cz.jurca.fieldreservationsystem.repository.adapter.UserDbAdapter
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
                RawPassword(basicAuthInput.password),
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
                    when (val constructedEmail = Email(registrationInput.email)) {
                        is Either.Left ->
                            when (constructedEmail.value) {
                                is EmptyEmailName -> {
                                    return ServerResponse.badRequest().bodyValueAndAwait(
                                        RegistrationOutput("Email cannot be empty."),
                                    )
                                }

                                is InvalidEmailFormat -> {
                                    return ServerResponse.badRequest().bodyValueAndAwait(
                                        RegistrationOutput("Invalid email format."),
                                    )
                                }
                            }

                        is Either.Right -> constructedEmail.value
                    },
                password = EncodedPassword(passwordEncoder.encode(registrationInput.password)),
                role = UserRole.valueOf(registrationInput.role.uppercase()),
                registrationProvider = userDbAdapter::create,
                usernameExistsProvider = userDbAdapter::existsByUsername,
                emailExistsProvider = userDbAdapter::existsByEmail,
            ).register().let { registrationResult ->
                when (registrationResult) {
                    is Either.Left -> {
                        when (registrationResult.value) {
                            is RegisterAsAdminError -> ServerResponse.badRequest().bodyValueAndAwait(RegistrationOutput(registrationResult.value.message))
                            is EmailAlreadyExistsError -> ServerResponse.status(HttpStatus.CONFLICT).bodyValueAndAwait(RegistrationOutput(registrationResult.value.message))
                            is UsernameAlreadyExistsError -> ServerResponse.status(HttpStatus.CONFLICT).bodyValueAndAwait(RegistrationOutput(registrationResult.value.message))
                        }
                    }
                    is Either.Right -> ServerResponse.created(URI.create("/")).bodyValueAndAwait(RegistrationOutput("User [${registrationResult.value.username.value}] registered successfully."))
                }
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