package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.codegen.types.EditSportsFieldInput
import cz.jurca.fieldreservationsystem.codegen.types.EditSportsFieldResult
import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Coordinates
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.Latitude
import cz.jurca.fieldreservationsystem.domain.Longitude
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NotFoundError
import cz.jurca.fieldreservationsystem.domain.NotResourceOwnerError
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UpdatedSportsField
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter

typealias ApiNotFoundError = cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
typealias ApiNotResourceOwnerError = cz.jurca.fieldreservationsystem.codegen.types.NotResourceOwnerError

@DgsComponent
class EditSportsFieldMutation(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userProvider: ProvidesLoginUser,
    private val idValidator: IdValidator,
) {
    @DgsMutation
    suspend fun editSportsField(
        id: Int,
        input: EditSportsFieldInput,
    ): EditSportsFieldResult {
        val idResult = idValidator.existsSportsField(id)
        if (idResult is SportFieldNotFound) {
            return ApiNotFoundError({ "Sports field with id $id not found" })
        }

        return UpdatedSportsField(
            id = idResult as SportsFieldId,
            name = Name(input.name),
            coordinates = Coordinates(Latitude(input.coordinates.latitude), Longitude(input.coordinates.longitude)),
            description = input.description?.let { Description(it) },
            address = Address(City(input.city), Street(input.street), ZipCode(input.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(input.countryCode))) { "Fronted should always send correct code" }),
            sportTypes = input.sportTypes.map { SportType.fromApi(it) },
            loginUser = userProvider.getLoginUser().getOrThrow(),
            updateSportsFieldProvider = sportsFieldDbAdapter::update,
        ).update().fold(
            ifLeft = { error ->
                when (error) {
                    is NotResourceOwnerError -> ApiNotResourceOwnerError({ error.message })
                    is NotFoundError -> ApiNotFoundError({ "Sports field with id $id not found" })
                }
            },
            ifRight = { sportsField -> sportsField.toApi() },
        )
    }
}