package cz.jurca.fieldreservationsystem.api.reservation.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.codegen.types.CreateReservationInput
import cz.jurca.fieldreservationsystem.codegen.types.CreateReservationResult
import cz.jurca.fieldreservationsystem.repository.adapter.ReservationDbAdapter

@DgsComponent
class CreateReservationMutation(private val reservationDbAdapter: ReservationDbAdapter) {
    @DgsMutation
    suspend fun createReservation(
        @InputArgument input: CreateReservationInput,
    ): CreateReservationResult = TODO("Implement")
}