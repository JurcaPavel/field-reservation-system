package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class CountryTest : BaseTest() {
    @Test
    fun `given valid alpha code when finding country by code then correct country should be returned`() {
        Given()
        val alphaCode = Country.AlphaCode3("USA")

        When()
        val country = Country.findByCode(alphaCode)!!

        Then()
        country shouldBe Country.UNITED_STATES_OF_AMERICA
        country.alphaCode3.value shouldBe "USA"
    }

    @Test
    fun `given an invalid alpha code when finding country by code then null should be returned`() {
        Given()
        val alphaCode = Country.AlphaCode3("XYZ")

        When()
        val country = Country.findByCode(alphaCode)

        Then()
        country shouldBe null
    }

    @Test
    fun `given short alpha code string when creating alpha code 3 then exception should have correct message`() {
        Given()
        val tooShort = "US"

        When()
        val exceptionTooShort =
            shouldThrow<IllegalArgumentException> {
                Country.AlphaCode3(tooShort)
            }

        Then()
        exceptionTooShort.message shouldBe "Alpha code 3 must be exactly 3 characters long"
    }

    @Test
    fun `given long alpha code string when creating alpha code 3 then exception should have correct message`() {
        Given()
        val tooLong = "USAA"

        When()
        val exceptionTooLong =
            shouldThrow<IllegalArgumentException> {
                Country.AlphaCode3(tooLong)
            }

        Then()
        exceptionTooLong.message shouldBe "Alpha code 3 must be exactly 3 characters long"
    }
}