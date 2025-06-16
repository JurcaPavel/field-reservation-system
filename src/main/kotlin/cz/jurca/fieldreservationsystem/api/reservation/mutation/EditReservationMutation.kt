package cz.jurca.fieldreservationsystem.api.reservation.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.codegen.types.EditReservationInput
import cz.jurca.fieldreservationsystem.codegen.types.EditReservationResult
import cz.jurca.fieldreservationsystem.db.adapter.ReservationDbAdapter

@DgsComponent
class EditReservationMutation(private val reservationDbAdapter: ReservationDbAdapter) {
    @DgsMutation
    suspend fun editReservation(
        @InputArgument input: EditReservationInput,
    ): EditReservationResult = TODO("Implement")
}