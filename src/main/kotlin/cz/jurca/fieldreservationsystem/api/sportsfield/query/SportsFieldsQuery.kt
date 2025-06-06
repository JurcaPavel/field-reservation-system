package cz.jurca.fieldreservationsystem.api.sportsfield.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import cz.jurca.fieldreservationsystem.codegen.types.Coordinates
import cz.jurca.fieldreservationsystem.codegen.types.Country
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInfo
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldFiltersInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldSortByInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldsWrapper
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.SportsFieldFilter
import cz.jurca.fieldreservationsystem.domain.UnloadedFilteredPage
import cz.jurca.fieldreservationsystem.repository.adapter.SportsFieldDbAdapter

@DgsComponent
class SportsFieldsQuery(private val sportsFieldDbAdapter: SportsFieldDbAdapter) {
    @DgsQuery
    suspend fun sportsFields(
        filters: SportsFieldFiltersInput?,
        sortBy: SportsFieldSortByInput?,
        pagination: PaginationInput,
    ): SportsFieldsWrapper =
        UnloadedFilteredPage(
            pageNumber = pagination.pageNumber,
            pageSize = pagination.pageSize,
            filters =
                SportsFieldFilter(
                    city = filters?.city?.let { City(it) },
                    countryCode = filters?.countryCode?.let { cz.jurca.fieldreservationsystem.domain.Country.AlphaCode3(it) },
                    sportTypes = filters?.sportTypes?.map { cz.jurca.fieldreservationsystem.domain.SportType.fromApi(it) },
                ),
            dataLoader = sportsFieldDbAdapter::filterSportsFields,
            // loginUser = userProvider.getLoginUser().getOrThrow(),
            sortBy =
                sortBy?.let { sportsFieldSortByInput ->
                    UnloadedFilteredPage.SportsFieldSortBy(
                        field = sportsFieldSortByInput.field,
                        direction = sportsFieldSortByInput.direction,
                    )
                },
        ).getData().let { page ->
            SportsFieldsWrapper.Builder()
                .withPaginationInfo(PaginationInfo.Builder().withItemsTotalCount(page.totalItems).build())
                .withSportsFields(
                    page.items.map { sportsField ->
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
                    },
                )
                .build()
        }
}