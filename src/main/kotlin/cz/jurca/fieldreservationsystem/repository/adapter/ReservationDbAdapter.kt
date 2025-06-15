package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.domain.Reservation
import cz.jurca.fieldreservationsystem.domain.ReservationId
import cz.jurca.fieldreservationsystem.domain.UnvalidatedReservationId
import cz.jurca.fieldreservationsystem.repository.ReservationRepository
import org.springframework.stereotype.Component

@Component
class ReservationDbAdapter(
    private val reservationRepository: ReservationRepository,
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userDbAdapter: UserDbAdapter,
) {
    suspend fun findReservation(id: UnvalidatedReservationId): ReservationId? =
        reservationRepository.findById(id.value)
            ?.run { ReservationId(this.getDaoId().value, this@ReservationDbAdapter::getDetail) }

    suspend fun getDetail(id: ReservationId): Reservation =
        requireNotNull(reservationRepository.findById(id.value)).let { dao ->
            dao.toDomain(
                id = id,
                ownerId = userDbAdapter.getId(dao.getOwnerDaoId()),
                sportsFieldId = sportsFieldDbAdapter.getId(dao.getSportsFieldDaoId()),
            )
        }
}