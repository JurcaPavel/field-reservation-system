package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler
import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler.BasicAuthInput
import io.kotest.matchers.shouldBe

class AuthenticationIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given user in db, when authenticate with correct credentials, response should be ok and basic outh returned in header`() {
        Given()
        When()
        val response =
            webTestClient.post()
                .uri("/public/authenticate")
                .bodyValue(BasicAuthInput(username = "pja", password = "userpassword"))
                .exchange()
                .expectStatus().isOk
                .expectBody(UserHandler.BasicAuthOutput::class.java)
                .returnResult()

        response.responseBody shouldBe UserHandler.BasicAuthOutput(status = "Valid credentials")
        response.responseHeaders.getFirst("Authorization") shouldBe "Basic cGphOnVzZXJwYXNzd29yZA=="
    }

    @Test
    fun `given user in db, when authenticate with invalid credentials, response should be unauthorized`() {
        Given()
        When()
        val response =
            webTestClient.post()
                .uri("/public/authenticate")
                .bodyValue(BasicAuthInput(username = "random", password = "randompassword"))
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody(UserHandler.BasicAuthOutput::class.java)
                .returnResult()

        response.responseBody shouldBe UserHandler.BasicAuthOutput(status = "Invalid credentials")
        response.responseHeaders.getFirst("Authorization") shouldBe null
    }
}