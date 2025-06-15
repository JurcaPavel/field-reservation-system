package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.cache.CacheProvider
import cz.jurca.fieldreservationsystem.cache.SPORTS_FIELD_KEY
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
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UpdatedSportsField
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.domain.error.ApiNotFoundError
import cz.jurca.fieldreservationsystem.domain.error.ApiNotResourceOwnerError
import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter

@DgsComponent
class EditSportsFieldMutation(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userProvider: ProvidesLoginUser,
    private val idValidator: IdValidator,
    private val cacheProvider: CacheProvider,
) {
    @DgsMutation
    suspend fun editSportsField(
        @InputArgument id: Int,
        @InputArgument input: EditSportsFieldInput,
    ): EditSportsFieldResult {
        val validationResult = idValidator.existsSportsField(id)
        if (validationResult is SportFieldNotFound) {
            return ApiNotFoundError({ "Sports field with id $id not found" })
        }

        return UpdatedSportsField(
            id = validationResult as SportsFieldId,
            name = Name(input.name),
            coordinates = Coordinates(Latitude(input.coordinates.latitude), Longitude(input.coordinates.longitude)),
            description = input.description?.let { Description(it) },
            address = Address(City(input.city), Street(input.street), ZipCode(input.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(input.countryCode))) { "Fronted should always send correct code" }),
            sportTypes = input.sportTypes.map { SportType.fromApi(it) },
            loginUser = userProvider.getLoginUser().getOrThrow(),
            updateSportsFieldProvider = sportsFieldDbAdapter::update,
        ).update().fold(
            ifLeft = { notResourceOwnerError -> ApiNotResourceOwnerError({ notResourceOwnerError.message }) },
            ifRight = { sportsField -> cacheProvider.put(SPORTS_FIELD_KEY + id.toString(), sportsField.toApi()) },
        )
    }
}