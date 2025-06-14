package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.cache.CacheProvider
import cz.jurca.fieldreservationsystem.codegen.types.CreateSportsFieldInput
import cz.jurca.fieldreservationsystem.codegen.types.CreateSportsFieldResult
import cz.jurca.fieldreservationsystem.codegen.types.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Coordinates
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Latitude
import cz.jurca.fieldreservationsystem.domain.Longitude
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.NewSportsField
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.ZipCode
import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter

@DgsComponent
class CreateSportsFieldMutation(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userProvider: ProvidesLoginUser,
    private val cacheProvider: CacheProvider,
) {
    @DgsMutation
    suspend fun createSportsField(
        @InputArgument input: CreateSportsFieldInput,
    ): CreateSportsFieldResult =
        NewSportsField(
            name = Name(input.name),
            coordinates = Coordinates(Latitude(input.coordinates.latitude), Longitude(input.coordinates.longitude)),
            description = input.description?.let { Description(it) },
            address = Address(City(input.city), Street(input.street), ZipCode(input.zipCode), requireNotNull(Country.findByCode(Country.AlphaCode3(input.countryCode))) { "Fronted should always send correct code" }),
            sportTypes = input.sportTypes.map { SportType.fromApi(it) },
            loginUser = userProvider.getLoginUser().getOrThrow(),
            createSportsFieldProvider = sportsFieldDbAdapter::create,
        ).create().fold(
            ifLeft = { error -> NotManagerOrAdminError({ error.message }) },
            ifRight = { sportsField -> cacheProvider.put(sportsField.id.value.toString(), sportsField.toApi()) },
        )
}