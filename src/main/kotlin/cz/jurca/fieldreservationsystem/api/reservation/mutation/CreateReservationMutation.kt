package cz.jurca.fieldreservationsystem.api.reservation.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.codegen.types.CreateReservationInput
import cz.jurca.fieldreservationsystem.codegen.types.CreateReservationResult
import cz.jurca.fieldreservationsystem.domain.DateTime
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.NewReservation
import cz.jurca.fieldreservationsystem.domain.Note
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.TimeSlot
import cz.jurca.fieldreservationsystem.domain.error.ApiNotFoundError
import cz.jurca.fieldreservationsystem.db.adapter.ReservationDbAdapter

@DgsComponent
class CreateReservationMutation(
    private val reservationDbAdapter: ReservationDbAdapter,
    private val idValidator: IdValidator,
    private val userProvider: ProvidesLoginUser,
) {
    @DgsMutation
    suspend fun createReservation(
        @InputArgument input: CreateReservationInput,
    ): CreateReservationResult {
        val sportsFieldId =
            when (val validatedSportsField = idValidator.existsSportsField(input.sportsFieldId.toInt())) {
                is SportsFieldId -> validatedSportsField
                SportFieldNotFound -> return ApiNotFoundError({ "Sports field with id ${input.sportsFieldId} not found. " })
            }

        return NewReservation(
            ownerId = userProvider.getLoginUser().getOrThrow().id,
            sportsFieldId = sportsFieldId,
            timeSlot = TimeSlot(DateTime(input.timeslot.startTime), DateTime(input.timeslot.endTime)),
            userNote = input.userNote?.let { Note(it) },
            fieldManagerNote = input.fieldManagerNote?.let { Note(it) },
            createReservationProvider = reservationDbAdapter::create,
            isAlreadyReserved = reservationDbAdapter::isAlreadyReserved,
        ).create().fold(
            ifLeft = { alreadyReservedError -> alreadyReservedError.toApi() },
            ifRight = { reservation -> reservation.toApi() },
        )
    }
}