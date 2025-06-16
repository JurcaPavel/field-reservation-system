package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class UsernameTest : BaseTest() {
    @Test
    fun `given username is blank when create it then should throw an exception`() {
        shouldThrow<IllegalArgumentException> {
            Username("")
        }
    }

    @Test
    fun `given username is longer than 50 chars when create it then should throw an exception`() {
        shouldThrow<IllegalArgumentException> {
            Username("012345678901234567890123456789012345678901234567891")
        }
    }

    @Test
    fun `given username is ok when create it then it should be created correctly`() {
        Given()
        When()
        val username = Username("validUsername")

        Then()
        username.value shouldBe "validUsername"
    }
}