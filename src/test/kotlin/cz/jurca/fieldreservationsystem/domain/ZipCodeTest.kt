package cz.jurca.fieldreservationsystem.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ZipCodeTest : StringSpec({
    "zip code creation should validate input" {
        forAll(
            row("76005", false),
            row("", true),
            row("   ", true),
        ) { zipCode, shouldThrow ->
            if (shouldThrow) {
                val exception = shouldThrow<IllegalArgumentException> { ZipCode(zipCode) }
                exception.message shouldBe "Zip code cannot be blank"
            } else {
                ZipCode(zipCode).value shouldBe zipCode
            }
        }
    }
})