package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class EmailTest : BaseTest() {
    @Test
    fun `given blank or empty email when creating email then it should return left with EmptyEmailName`() {
        Given()
        forAll(
            row(""),
            row("   "),
        ) { emptyEmail ->
            When()
            val result = Email(emptyEmail)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<EmailValidationError.EmptyEmailName>()
                message shouldBe "Email cannot be empty."
            }
        }
    }

    @Test
    fun `given invalid email format when creating email then it should return left with InvalidEmailFormat`() {
        Given()
        forAll(
            row("test@"),
            row("test"),
            row("@example.com"),
            row("test@example@com"),
            row("test@example."),
            row(".test@example.com"),
        ) { invalidEmail ->
            When()
            val result = Email(invalidEmail)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<EmailValidationError.InvalidEmailFormat>()
                message shouldBe "Invalid email format."
            }
        }
    }

    @Test
    fun `given various valid emails when creating email then it should return right with email`() {
        Given()
        forAll(
            row("test@example.com"),
            row("user.name@domain.co.uk"),
            row("user+tag@example.org"),
            row("user-name@example.net"),
            row("user_name@example.io"),
            row("123@example.com"),
        ) { validEmail ->
            When()
            val result = Email(validEmail)

            Then()
            result.shouldBeRight().value shouldBe validEmail
        }
    }
}