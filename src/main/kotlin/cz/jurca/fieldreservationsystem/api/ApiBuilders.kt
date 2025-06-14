package cz.jurca.fieldreservationsystem.api

import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.codegen.types.Success

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