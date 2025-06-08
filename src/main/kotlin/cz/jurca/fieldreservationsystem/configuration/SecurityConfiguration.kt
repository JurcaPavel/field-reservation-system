package cz.jurca.fieldreservationsystem.configuration

import cz.jurca.fieldreservationsystem.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(
    @Value("\${frontend.domain}") val frontendDomain: String,
    private val userRepository: UserRepository,
) {
    @Bean
    fun apiHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf { csrfSpec ->
                csrfSpec.disable()
            }.cors { corsSpec ->
                corsSpec.configurationSource {
                    val cors = CorsConfiguration()
                    cors.allowedOrigins = listOf(frontendDomain)
                    cors.allowedMethods = listOf("*")
                    cors.allowedHeaders = listOf("*")
                    cors.exposedHeaders = listOf(HttpHeaders.AUTHORIZATION)
                    cors.allowCredentials = true
                    cors
                }
            }.authorizeExchange { exchanges ->
                exchanges.pathMatchers("/public/authenticate").permitAll()
                // todo: setup security
                exchanges.pathMatchers("/graphiql").permitAll()
                exchanges.pathMatchers("/public/graphql").permitAll()
                exchanges.pathMatchers("/management/**").permitAll()
                exchanges.pathMatchers("/private/**").permitAll()
                exchanges.pathMatchers("/**").authenticated()
            }.httpBasic(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService = UserDetailsService(userRepository)

    class UserDetailsService(
        private val repository: UserRepository,
    ) : ReactiveUserDetailsService {
        override fun findByUsername(username: String): Mono<UserDetails> =
            repository.findByUsername(username)
                .mapNotNull { userDao ->
                    userDao?.let {
                        CustomUserDetails(
                            id = userDao.getDaoId(),
                            username = userDao.username,
                            password = userDao.password,
                            role = "ROLE_" + userDao.role,
                        )
                    }
                }
    }

    class CustomUserDetails(
        private val id: Int,
        username: String,
        password: String?,
        role: String,
    ) : User(username, password, mutableListOf(GrantedAuthority { role })) {
        fun getId(): Int = id
    }
}