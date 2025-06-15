package cz.jurca.fieldreservationsystem.domain

import java.time.OffsetDateTime

class TimeSlot(
    val startTime: DateTime,
    val endTime: DateTime,
) {
    init {
        require(startTime.value.isBefore(endTime.value)) {
            throw IllegalArgumentException("startTime must be before endTime")
        }
        require(startTime.value.isAfter(OffsetDateTime.now())) {
            throw IllegalArgumentException("startTime must be in the future")
        }
    }
}