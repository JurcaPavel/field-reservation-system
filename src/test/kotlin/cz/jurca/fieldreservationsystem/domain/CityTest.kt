package cz.jurca.fieldreservationsystem.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class CityTest : StringSpec({
    "city creation should validate input" {
        forAll(
            row("Prague", false),
            row("", true),
            row("   ", true)
        ) { cityName, shouldThrow ->
            if (shouldThrow) {
                val exception = shouldThrow<IllegalArgumentException> { City(cityName) }
                exception.message shouldBe "City cannot be blank"
            } else {
                City(cityName).value shouldBe cityName
            }
        }
    }
})