package cz.jurca.fieldreservationsystem.domain

class SportsFieldFilter(
    val city: City?,
    val countryCode: Country.AlphaCode3?,
    val sportTypes: List<SportType>?,
)