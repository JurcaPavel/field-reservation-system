package cz.jurca.fieldreservationsystem.domain

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class DateTime(
    val value: OffsetDateTime,
) {
    private val formatterDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun toDateTimeString(): String = value.format(formatterDateTime)
}