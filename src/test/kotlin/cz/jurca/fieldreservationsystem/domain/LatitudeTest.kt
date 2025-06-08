package cz.jurca.fieldreservationsystem.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class LatitudeTest : StringSpec({
    "latitude creation should succeed with valid values" {
        forAll(
            row(0.0),
            row(90.0),
            row(-90.0),
            row(45.5),
            row(-23.7),
        ) { value ->
            val latitude = Latitude(value)
            latitude.value shouldBe value
        }
    }

    "latitude creation should fail with wrong values" {
        forAll(
            row(-90.1),
            row(90.1),
        ) { value ->
            val exception =
                shouldThrow<IllegalArgumentException> {
                    Latitude(value)
                }
            exception.message shouldBe "Latitude must be between -90 and 90 degrees."
        }
    }
})