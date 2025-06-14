package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RestRouter {
    @Bean
    fun mainRouter(userHandler: UserHandler) =
        coRouter {
            POST("/public/v1/authenticate", userHandler::authenticate)
        }
}