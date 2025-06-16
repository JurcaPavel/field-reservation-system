package cz.jurca.fieldreservationsystem.domain

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

class BasicAuthentication(
    private val username: Username,
    private val rawPassword: AuthenticationPassword,
    private val findByUsernameProvider: suspend (String) -> Mono<UserDetails>,
    private val passwordMatchesProvider: suspend (rawPassword: CharSequence, encodedPassword: String) -> Boolean,
) {
    suspend fun authenticate(): AuthResult =
        findByUsernameProvider(username.value).awaitSingleOrNull()?.let { userDetails ->
            passwordMatchesProvider(rawPassword.value, userDetails.password)
        }.let { isValidUser ->
            if (isValidUser == true) {
                AuthResult.Success(username, AuthResult.AuthStatus("Valid credentials"))
            } else {
                AuthResult.Failure(AuthResult.AuthFailureReason("Invalid credentials"))
            }
        }
}

sealed class AuthResult {
    data class Success(val username: Username, val status: AuthStatus) : AuthResult()

    data class Failure(val reason: AuthFailureReason) : AuthResult()

    @JvmInline
    value class AuthStatus(val value: String)

    @JvmInline
    value class AuthFailureReason(val value: String)
}