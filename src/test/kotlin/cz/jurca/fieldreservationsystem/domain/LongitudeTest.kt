package cz.jurca.fieldreservationsystem.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class LongitudeTest : StringSpec({
    "longitude creation should succeed with valid values" {
        forAll(
            row(0.0),
            row(180.0),
            row(-180.0),
            row(123.45),
            row(-45.67),
        ) { value ->
            val longitude = Longitude(value)
            longitude.value shouldBe value
        }
    }

    "longitude creation should fail with wrong values" {
        forAll(
            row(-180.1),
            row(180.1),
        ) { value ->
            val exception =
                shouldThrow<IllegalArgumentException> {
                    Longitude(value)
                }
            exception.message shouldBe "Longitude must be between -180 and 180 degrees."
        }
    }
})