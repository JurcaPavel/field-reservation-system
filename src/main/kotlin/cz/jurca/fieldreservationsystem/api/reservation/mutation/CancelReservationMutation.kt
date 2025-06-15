package cz.jurca.fieldreservationsystem.api.reservation.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.codegen.types.CancelReservationResult
import cz.jurca.fieldreservationsystem.repository.adapter.ReservationDbAdapter

@DgsComponent
class CancelReservationMutation(private val reservationDbAdapter: ReservationDbAdapter) {
    @DgsMutation
    suspend fun cancelReservation(
        @InputArgument id: Int,
    ): CancelReservationResult = TODO("Implement")
}