package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class OpenApiConfiguration {
    @RouterOperations(
        RouterOperation(
            path = "/public/v1/authenticate",
            method = arrayOf(RequestMethod.POST),
            operation =
                Operation(
                    tags = ["Authentication"],
                    operationId = "authenticate",
                    summary = "User authentication",
                    description = "Authenticate user with username and password. Returns basic auth in header if successful.",
                    requestBody =
                        RequestBody(
                            required = true,
                            content = [
                                Content(
                                    schema = Schema(implementation = UserHandler.BasicAuthInput::class),
                                ),
                            ],
                        ),
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "successful authentication",
                            content = [Content(schema = Schema(implementation = UserHandler.BasicAuthOutput::class))],
                            headers = [Header(name = "Authorization", description = "Basic authentication header")],
                        ),
                        ApiResponse(
                            responseCode = "401",
                            description = "unauthorized",
                        ),
                    ],
                ),
        ),
        RouterOperation(
            path = "/public/v1/register",
            method = arrayOf(RequestMethod.POST),
            operation =
                Operation(
                    tags = ["Registration"],
                    operationId = "register",
                    summary = "User registration",
                    description = "Register a new user with name, username, email, password, and role.",
                    requestBody =
                        RequestBody(
                            required = true,
                            content = [
                                Content(
                                    schema = Schema(implementation = UserHandler.RegistrationInput::class),
                                ),
                            ],
                        ),
                    responses = [
                        ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully",
                            content = [Content(schema = Schema(implementation = UserHandler.RegistrationOutput::class))],
                        ),
                        ApiResponse(
                            responseCode = "400",
                            description = "Bad request - invalid email, empty email, registering as admin",
                            content = [Content(schema = Schema(implementation = UserHandler.RegistrationOutput::class))],
                        ),
                        ApiResponse(
                            responseCode = "409",
                            description = "Conflict - username or email already exists",
                            content = [Content(schema = Schema(implementation = UserHandler.RegistrationOutput::class))],
                        ),
                    ],
                ),
        ),
    )
    @Bean
    fun openApiConfig(): RouterFunction<ServerResponse> {
        // Only used for OpenAPI documentation separation from router
        return route().GET("/dummy-openapi-path") { ServerResponse.ok().build() }.build()
    }
}