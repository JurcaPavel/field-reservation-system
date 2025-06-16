package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler.RegistrationInput
import cz.jurca.fieldreservationsystem.api.user.handler.UserHandler.RegistrationOutput
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus

class RegistrationIntegrationTest : BaseIntegrationTest() {
    private val strongPassword: String = "strongPassword123!*"

    @Test
    fun `given valid registration data, when register, then response should be created and user should be in db`() {
        Given()
        val registrationInput =
            RegistrationInput(
                name = "Test User",
                username = "testuser",
                email = "test@example.com",
                password = strongPassword,
                role = "BASIC",
            )

        When()
        val response =
            webTestClient.post()
                .uri("/public/v1/register")
                .bodyValue(registrationInput)
                .exchange()
                .expectStatus().isCreated
                .expectBody(RegistrationOutput::class.java)
                .returnResult()

        Then()
        response.responseBody shouldBe
            RegistrationOutput(
                message = "User [testuser] registered successfully.",
            )

        And("User should be in database")
        runBlocking {
            repository.findAllUsers().shouldHaveSize(3).filter { it.username == "testuser" }.size shouldBe 1
        }
    }

    @Test
    fun `given registration data with already existing username, when register, then response should have status conflict`() {
        Given()
        val registrationInput =
            RegistrationInput(
                name = "Test User",
                username = "pjm",
                email = "test@example.com",
                password = strongPassword,
                role = "MANAGER",
            )

        When()
        val response =
            webTestClient.post()
                .uri("/public/v1/register")
                .bodyValue(registrationInput)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(RegistrationOutput::class.java)
                .returnResult()

        Then()
        response.responseBody?.message shouldBe "User with username [pjm] already exists!"
    }

    @Test
    fun `given registration data with already existing email, when register, then response should have status conflict`() {
        Given()
        val registrationInput =
            RegistrationInput(
                name = "Test User",
                username = "testuser2",
                email = "manager@email.com",
                password = strongPassword,
                role = "MANAGER",
            )

        When()
        val response =
            webTestClient.post()
                .uri("/public/v1/register")
                .bodyValue(registrationInput)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(RegistrationOutput::class.java)
                .returnResult()

        Then()
        response.responseBody?.message shouldBe "User with email [manager@email.com] already exists!"
    }

    @Test
    fun `given registration data with invalid email, when register, then response should have status bad request`() {
        Given()
        val registrationInput =
            RegistrationInput(
                name = "Test User",
                username = "testuser3",
                email = "invalid-email",
                password = strongPassword,
                role = "BASIC",
            )

        When()
        val response =
            webTestClient.post()
                .uri("/public/v1/register")
                .bodyValue(registrationInput)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(RegistrationOutput::class.java)
                .returnResult()

        Then()
        response.responseBody?.message shouldBe "Invalid email format."
    }

    @Test
    fun `given registration data with admin role, when register, response should have status bad request`() {
        Given()
        val registrationInput =
            RegistrationInput(
                name = "Test User",
                username = "testuser4",
                email = "test4@example.com",
                password = strongPassword,
                role = "ADMIN",
            )

        When()
        val response =
            webTestClient.post()
                .uri("/public/v1/register")
                .bodyValue(registrationInput)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(RegistrationOutput::class.java)
                .returnResult()

        Then()
        response.responseBody?.message shouldBe "Cannot register as admin!"
    }
}