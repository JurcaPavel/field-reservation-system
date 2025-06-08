package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import cz.jurca.fieldreservationsystem.codegen.types.CreateSportsFieldInput
import cz.jurca.fieldreservationsystem.codegen.types.CreateSportsFieldResult
import cz.jurca.fieldreservationsystem.codegen.types.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Coordinates
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Latitude
import cz.jurca.fieldreservationsystem.domain.Longitude
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NewOrUpdatedSportsField
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter

@DgsComponent
class CreateSportsFieldMutation(private val sportsFieldDbAdapter: SportsFieldDbAdapter) {
    @DgsMutation
    suspend fun createSportsField(input: CreateSportsFieldInput): CreateSportsFieldResult =
        NewOrUpdatedSportsField(
            name = Name(input.name),
            coordinates = Coordinates(Latitude(input.coordinates.latitude), Longitude(input.coordinates.longitude)),
            description = input.description?.let { Description(it) },
            address = Address(City(input.city), Street(input.street), ZipCode(input.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(input.countryCode))) { "Fronted should always send correct code" }),
            sportTypes = input.sportTypes.map { SportType.fromApi(it) },
            createSportsFieldProvider = sportsFieldDbAdapter::create,
            updateSportsFieldProvider = sportsFieldDbAdapter::update,
        ).create().fold(
            ifLeft = { error ->
                NotManagerOrAdminError({ error.message })
            },
            ifRight = { sportsField ->
                SportsField(
                    id = { sportsField.id.value.toString() },
                    name = { sportsField.name.value },
                    sportTypes = { sportsField.sportTypes.map { it.toApi() } },
                    coordinates = { cz.jurca.fieldreservationsystem.codegen.types.Coordinates({ sportsField.coordinates.latitude.value }, { sportsField.coordinates.longitude.value }) },
                    city = { sportsField.address.city.value },
                    street = { sportsField.address.street.value },
                    zipCode = { sportsField.address.zipCode.value },
                    country = { cz.jurca.fieldreservationsystem.codegen.types.Country({ sportsField.address.country.alphaCode3.value }, { sportsField.address.country.countryName.value }) },
                    description = { sportsField.description?.value },
                )
            },
        )
}