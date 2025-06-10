package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import cz.jurca.fieldreservationsystem.domain.EmptyEmailName.message
import cz.jurca.fieldreservationsystem.domain.InvalidEmailFormat.message
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.common.runBlocking
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class EmailTest : BaseTest() {
    @Test
    fun `given blank or empty email when creating email then it should return left with EmptyEmailName`() =
        runBlocking {
            Given()
            forAll(
                row(""),
                row("   "),
            ) { emptyEmail ->
                When()
                val result = Email(emptyEmail)

                Then()
                result.shouldBeLeft().run {
                    shouldBeTypeOf<EmptyEmailName>()
                    message shouldBe "Email is empty"
                }
            }
        }

    @Test
    fun `given invalid email format when creating email then it should return left with InvalidEmailFormat`() =
        runBlocking {
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
                    shouldBeTypeOf<InvalidEmailFormat>()
                    message shouldBe "Email has an invalid format"
                }
            }
        }

    @Test
    fun `given various valid emails when creating email then it should return right with email`() =
        runBlocking {
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