package cz.jurca.fieldreservationsystem.api.user.route

import cz.jurca.fieldreservationsystem.domain.AuthResult.Failure
import cz.jurca.fieldreservationsystem.domain.AuthResult.Success
import cz.jurca.fieldreservationsystem.domain.BasicAuthentication
import cz.jurca.fieldreservationsystem.domain.RawPassword
import cz.jurca.fieldreservationsystem.domain.Username
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRoutesConfig {
    @Bean
    fun userRoutes(
        userDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder,
    ) = coRouter {
        POST("/public/authenticate") { request ->
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
                            ServerResponse.ok().headers { it.addAll(headers) }.bodyValueAndAwait(BasicAuthOutput("Valid credentials"))
                        }

                        is Failure -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(BasicAuthOutput(authResult.reason))
                    }
                }
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