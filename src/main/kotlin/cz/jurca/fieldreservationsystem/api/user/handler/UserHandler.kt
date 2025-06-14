package cz.jurca.fieldreservationsystem.api.user.handler

import cz.jurca.fieldreservationsystem.domain.AuthResult.Failure
import cz.jurca.fieldreservationsystem.domain.AuthResult.Success
import cz.jurca.fieldreservationsystem.domain.BasicAuthentication
import cz.jurca.fieldreservationsystem.domain.RawPassword
import cz.jurca.fieldreservationsystem.domain.Username
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userDetailsService: ReactiveUserDetailsService,
    private val passwordEncoder: PasswordEncoder,
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

    data class BasicAuthInput(
        val username: String,
        val password: String,
    )

    data class BasicAuthOutput(
        val status: String,
    )
}