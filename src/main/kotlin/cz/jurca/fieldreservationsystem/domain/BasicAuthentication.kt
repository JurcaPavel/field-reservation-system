package cz.jurca.fieldreservationsystem.domain

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

class BasicAuthentication(
    private val username: Username,
    private val rawPassword: RawPassword,
    private val findByUsernameProvider: suspend (String) -> Mono<UserDetails>,
    private val passwordMatchesProvider: suspend (rawPassword: CharSequence, encodedPassword: String) -> Boolean,
) {
    suspend fun authenticate(): AuthResult =
        findByUsernameProvider(username.value).awaitSingleOrNull()?.let { userDetails ->
            passwordMatchesProvider(rawPassword.value, userDetails.password)
        }.let { isValidUser ->
            if (isValidUser == true) {
                AuthResult.Success(username)
            } else {
                AuthResult.Failure("Invalid credentials")
            }
        }
}

sealed class AuthResult {
    data class Success(val username: Username) : AuthResult()

    data class Failure(val reason: String) : AuthResult()
}