package cz.jurca.fieldreservationsystem.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class StreetTest : StringSpec({
    "street creation should validate input" {
        forAll(
            row("Tomáše Bati", false),
            row("", true),
            row("   ", true),
        ) { streetName, shouldThrow ->
            if (shouldThrow) {
                val exception = shouldThrow<IllegalArgumentException> { Street(streetName) }
                exception.message shouldBe "Street cannot be blank"
            } else {
                Street(streetName).value shouldBe streetName
            }
        }
    }
})