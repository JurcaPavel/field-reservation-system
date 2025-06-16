package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.configuration.SecurityConfiguration
import cz.jurca.fieldreservationsystem.db.adapter.SportsFieldDbAdapter
import cz.jurca.fieldreservationsystem.db.adapter.UserDbAdapter
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

interface ProvidesLoginUser {
    suspend fun getLoginUser(): Result<LoginUser>
}

@Component
class UserProvider(private val sportsFieldDbAdapter: SportsFieldDbAdapter, private val userDbAdapter: UserDbAdapter) : ProvidesLoginUser {
    override suspend fun getLoginUser(): Result<LoginUser> {
        val auth: Authentication =
            ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()?.authentication
                ?: return Result.failure(AuthenticationCredentialsNotFoundException("User is not authenticated."))
        val springUser = auth.principal as SecurityConfiguration.CustomUserDetails
        val loginUser =
            LoginUser(
                requireNotNull(UnvalidatedUserId(springUser.getId()).validate(userDbAdapter::findUser).getOrNull()) { "This should never happen as user is already authenticated. " },
                Username(springUser.username),
                UserRole.fromSpringRole(springUser.authorities.first().authority),
                sportsFieldDbAdapter::isSportsFieldOwner,
            )
        return Result.success(loginUser)
    }
}