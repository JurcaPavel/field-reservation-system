package cz.jurca.fieldreservationsystem.domain

data class Coordinates(
    val latitude: Latitude,
    val longitude: Longitude,
)

@JvmInline value class Latitude(val value: Double) {
    init {
        require(value in -90.0..90.0) { "Latitude must be between -90 and 90 degrees." }
    }
}

@JvmInline value class Longitude(val value: Double) {
    init {
        require(value in -180.0..180.0) { "Longitude must be between -180 and 180 degrees." }
    }
}