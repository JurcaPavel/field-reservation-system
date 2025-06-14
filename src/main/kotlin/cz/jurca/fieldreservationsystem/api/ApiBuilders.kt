package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.codegen.types.SportType
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.codegen.types.Success
import cz.jurca.fieldreservationsystem.domain.SportType.BASKETBALL
import cz.jurca.fieldreservationsystem.domain.SportType.BEACH_VOLLEYBALL
import cz.jurca.fieldreservationsystem.domain.SportType.SOCCER
import cz.jurca.fieldreservationsystem.domain.SportType.TENNIS

fun cz.jurca.fieldreservationsystem.domain.SportsField.toApi(): SportsField =
    SportsField(
        id = { this.id.value.toString() },
        name = { this.name.value },
        sportTypes = { this.sportTypes.map { it.toApi() } },
        coordinates = { cz.jurca.fieldreservationsystem.codegen.types.Coordinates({ this.coordinates.latitude.value }, { this.coordinates.longitude.value }) },
        city = { this.address.city.value },
        street = { this.address.street.value },
        zipCode = { this.address.zipCode.value },
        country = { cz.jurca.fieldreservationsystem.codegen.types.Country({ this.address.country.alphaCode3.value }, { this.address.country.countryName.value }) },
        description = { this.description?.value },
    )

fun cz.jurca.fieldreservationsystem.domain.Success.toApi(message: String): Success = Success( {message} )

fun cz.jurca.fieldreservationsystem.domain.SportType.toApi(): SportType {
    return when (this) {
        BASKETBALL -> cz.jurca.fieldreservationsystem.codegen.types.SportType.BASKETBALL
        BEACH_VOLLEYBALL -> cz.jurca.fieldreservationsystem.codegen.types.SportType.BEACH_VOLLEYBALL
        SOCCER -> cz.jurca.fieldreservationsystem.codegen.types.SportType.SOCCER
        TENNIS -> cz.jurca.fieldreservationsystem.codegen.types.SportType.TENNIS
    }
}