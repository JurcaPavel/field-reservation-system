package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class NewPasswordTest : BaseTest() {
    @Test
    fun `given password that is too short when creating password then it should return left with TooShort`() {
        Given()
        forAll(
            row(""),
            row("Aa1!"),
            row("Aa1!23"),
        ) { shortPassword ->
            When()
            val result = NewPassword(shortPassword)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<PasswordValidationError.TooShort>()
                message shouldBe "Password is too short, it has to be at least 8 characters long"
            }
        }
    }

    @Test
    fun `given password without digits when creating password then it should return left with NoDigit`() {
        Given()
        forAll(
            row("Password!"),
            row("PasswordTest!"),
        ) { passwordWithoutDigit ->
            When()
            val result = NewPassword(passwordWithoutDigit)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<PasswordValidationError.NoDigit>()
                message shouldBe "Password must contain at least one digit"
            }
        }
    }

    @Test
    fun `given password without uppercase letters when creating password then it should return left with NoUppercase`() {
        Given()
        forAll(
            row("password123!"),
            row("password!123"),
        ) { passwordWithoutUppercase ->
            When()
            val result = NewPassword(passwordWithoutUppercase)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<PasswordValidationError.NoUppercase>()
                message shouldBe "Password must contain at least one uppercase letter"
            }
        }
    }

    @Test
    fun `given password without lowercase letters when creating password then it should return left with NoLowercase`() {
        Given()
        forAll(
            row("PASSWORD123!"),
            row("PASSWORD!123"),
        ) { passwordWithoutLowercase ->
            When()
            val result = NewPassword(passwordWithoutLowercase)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<PasswordValidationError.NoLowercase>()
                message shouldBe "Password must contain at least one lowercase letter"
            }
        }
    }

    @Test
    fun `given password without special characters when creating password then it should return left with NoSpecialChar`() {
        Given()
        forAll(
            row("Password123"),
            row("123Password"),
        ) { passwordWithoutSpecialChar ->
            When()
            val result = NewPassword(passwordWithoutSpecialChar)

            Then()
            result.shouldBeLeft().run {
                shouldBeTypeOf<PasswordValidationError.NoSpecialChar>()
                message shouldBe "Password must contain at least one special character"
            }
        }
    }

    @Test
    fun `given valid passwords when creating password then it should return right with password`() {
        Given()
        forAll(
            row("Password123!"),
            row("StrongP@ssw0rd"),
            row("C0mpl3x!P@ss"),
            row("1Secure\$Password"),
            row("P@ssw0rd!123"),
        ) { validPassword ->
            When()
            val result = NewPassword(validPassword)

            Then()
            result.shouldBeRight().value shouldBe validPassword
        }
    }
}