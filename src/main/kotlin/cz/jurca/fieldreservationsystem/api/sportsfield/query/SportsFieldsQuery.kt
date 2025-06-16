package cz.jurca.fieldreservationsystem.api.sportsfield.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInfo
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldFiltersInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldSortByInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldsWrapper
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.SportsFieldFilter
import cz.jurca.fieldreservationsystem.domain.UnloadedFilteredPage
import cz.jurca.fieldreservationsystem.db.adapter.SportsFieldDbAdapter

@DgsComponent
class SportsFieldsQuery(private val sportsFieldDbAdapter: SportsFieldDbAdapter) {
    @DgsQuery
    suspend fun sportsFields(
        @InputArgument filters: SportsFieldFiltersInput?,
        @InputArgument sortBy: SportsFieldSortByInput?,
        @InputArgument pagination: PaginationInput,
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
            sortBy =
                sortBy?.let { sportsFieldSortByInput ->
                    UnloadedFilteredPage.SportsFieldSortBy(
                        field = sportsFieldSortByInput.field,
                        direction = sportsFieldSortByInput.direction,
                    )
                },
        ).getData().let { page ->
            SportsFieldsWrapper(
                sportsFields = { page.items.map { sportsField -> sportsField.toApi() } },
                paginationInfo = { PaginationInfo({ page.totalItems }) },
            )
        }
}