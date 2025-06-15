package cz.jurca.fieldreservationsystem.api.reservation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.cache.CacheProvider
import cz.jurca.fieldreservationsystem.cache.RESERVATION_KEY
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.Reservation
import cz.jurca.fieldreservationsystem.codegen.types.ReservationResult
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.ReservationId
import cz.jurca.fieldreservationsystem.domain.ReservationNotFound
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError

@DgsComponent
class ReservationQuery(private val idValidator: IdValidator, private val cacheProvider: CacheProvider, private val userProvider: ProvidesLoginUser) {
    @DgsQuery
    suspend fun reservation(
        @InputArgument id: Int,
    ): ReservationResult =
        cacheProvider.get(RESERVATION_KEY + id.toString(), Reservation::class.java)
            ?: when (val idResult = idValidator.existsReservation(id)) {
                is ReservationId -> {
                    idResult.getDetail(userProvider.getLoginUser().getOrThrow()).fold(
                        ifLeft = { notResourceOwnerError -> NotResourceOwnerError(notResourceOwnerError.message).toApi() },
                        ifRight = { reservation -> cacheProvider.put(RESERVATION_KEY + id.toString(), reservation.toApi()) },
                    )
                }

                is ReservationNotFound -> NotFoundError({ "Reservation with id $id not found" })
            }
}