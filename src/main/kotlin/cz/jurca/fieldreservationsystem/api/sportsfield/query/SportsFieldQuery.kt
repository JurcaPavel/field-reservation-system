package cz.jurca.fieldreservationsystem.api.sportsfield.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import cz.jurca.fieldreservationsystem.codegen.types.Coordinates
import cz.jurca.fieldreservationsystem.codegen.types.Country
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldResult
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportsFieldId

@DgsComponent
class SportsFieldQuery(private val validator: IdValidator) {
    @DgsQuery
    suspend fun sportsField(id: Int): SportsFieldResult =
        when (val idResult = validator.existsSportsField(id)) {
            is SportsFieldId ->
                idResult
                    .getDetail().let { sportsField ->
                        // TODO LATER INTRODUCE CUSTOM OBJECT, SO WE CAN PASS THE SPORTS FIELD DOMAIN OBJECT INTO CONSTRUCTOR
                        SportsField.Builder()
                            .withId(sportsField.id.value.toString())
                            .withName(sportsField.name.value)
                            .withSportTypes(sportsField.sportTypes.map { it.toApi() })
                            .withCoordinates(
                                Coordinates.Builder()
                                    .withLatitude(sportsField.latitude)
                                    .withLongitude(sportsField.longitude)
                                    .build(),
                            )
                            .withCity(sportsField.address.city.value)
                            .withStreet(sportsField.address.street?.value)
                            .withZipCode(sportsField.address.zipCode.value)
                            .withCountry(
                                Country.Builder()
                                    .withCode(sportsField.address.country.alphaCode3.value)
                                    .withName(sportsField.address.country.countryName.value)
                                    .build(),
                            )
                            .withDescription(sportsField.description?.value)
                            .build()
                    }

            is SportFieldNotFound -> NotFoundError.Builder().withMessage("Sports field with id $id not found").build()
        }
}