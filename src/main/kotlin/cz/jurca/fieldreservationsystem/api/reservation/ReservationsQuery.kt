package cz.jurca.fieldreservationsystem.api.reservation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInfo
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInput
import cz.jurca.fieldreservationsystem.codegen.types.ReservationFiltersInput
import cz.jurca.fieldreservationsystem.codegen.types.ReservationsWrapper
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.repository.adapter.ReservationDbAdapter

@DgsComponent
class ReservationsQuery(private val idValidator: IdValidator, private val reservationDbAdapter: ReservationDbAdapter) {
    @DgsQuery
    suspend fun reservations(
        @InputArgument filters: ReservationFiltersInput?,
        @InputArgument pagination: PaginationInput,
    ): ReservationsWrapper {
        // TODO implement
        return ReservationsWrapper(
            reservations = { listOf() },
            paginationInfo = { PaginationInfo({ 0 }) },
        )
    }
}