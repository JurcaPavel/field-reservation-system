package cz.jurca.fieldreservationsystem.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(
    @Value("\${frontend.domain}") val frontendDomain: String,
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
                exchanges.pathMatchers("/public/graphiql-dgs").permitAll()
                exchanges.pathMatchers("/public/graphql-dgs").permitAll()
                exchanges.pathMatchers("/management/**").permitAll()
                exchanges.pathMatchers("/private/**").permitAll()
                exchanges.pathMatchers("/**").authenticated()
            }.httpBasic(Customizer.withDefaults())

        return http.build()
    }
}