package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.time.OffsetDateTime

class TimeSlotTest : BaseTest() {
    @Test
    fun `given startTime is before endTime when create TimeSlot then create it correctly`() {
        Given()
        val startTime = DateTime(OffsetDateTime.now().plusHours(2))
        val endTime = DateTime(OffsetDateTime.now().plusHours(3))

        When()
        val timeSlot = TimeSlot(startTime = startTime, endTime = endTime)

        Then()
        timeSlot.startTime shouldBe startTime
        timeSlot.endTime shouldBe endTime
    }

    @Test
    fun `given startTime is after endTime when create TimeSlot then throw exception`() {
        Given()
        val startTime = DateTime(OffsetDateTime.now().plusHours(1))
        val endTime = DateTime(OffsetDateTime.now())

        When()
        shouldThrow<IllegalArgumentException> {
            TimeSlot(startTime = startTime, endTime = endTime)
        }
    }

    @Test
    fun `given startTime is same as endTime when create TimeSlot then throw exception`() {
        Given()
        val now = OffsetDateTime.now()
        val startTime = DateTime(now)
        val endTime = DateTime(now)

        When()
        shouldThrow<IllegalArgumentException> {
            TimeSlot(startTime = startTime, endTime = endTime)
        }
    }

    @Test
    fun `given startTime is in the past when create TimeSlot then throw exception`() {
        Given()
        val startTime = DateTime(OffsetDateTime.now().minusHours(1))
        val endTime = DateTime(OffsetDateTime.now())

        When()
        shouldThrow<IllegalArgumentException> {
            TimeSlot(startTime = startTime, endTime = endTime)
        }
    }
}