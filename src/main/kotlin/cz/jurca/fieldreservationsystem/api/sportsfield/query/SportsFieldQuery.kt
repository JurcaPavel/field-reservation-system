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
                        SportsField(
                            id = { sportsField.id.value.toString() },
                            name = { sportsField.name.value },
                            sportTypes = { sportsField.sportTypes.map { it.toApi() }},
                            coordinates = { Coordinates({sportsField.latitude}, {sportsField.longitude}) },
                            city = { sportsField.address.city.value },
                            street = { sportsField.address.street?.value },
                            zipCode = { sportsField.address.zipCode.value },
                            country = { Country({sportsField.address.country.alphaCode3.value}, {sportsField.address.country.countryName.value}) },
                            description = { sportsField.description?.value },
                        )
                    }

            is SportFieldNotFound -> NotFoundError.Builder().withMessage("Sports field with id $id not found").build()
        }
}