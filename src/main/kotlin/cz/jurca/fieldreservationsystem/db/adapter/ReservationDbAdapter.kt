package cz.jurca.fieldreservationsystem.db.adapter

import cz.jurca.fieldreservationsystem.db.repository.ReservationDao
import cz.jurca.fieldreservationsystem.db.repository.ReservationRepository
import cz.jurca.fieldreservationsystem.domain.NewReservation
import cz.jurca.fieldreservationsystem.domain.Reservation
import cz.jurca.fieldreservationsystem.domain.ReservationId
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.TimeSlot
import cz.jurca.fieldreservationsystem.domain.UnvalidatedReservationId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReservationDbAdapter(
    private val reservationRepository: ReservationRepository,
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userDbAdapter: UserDbAdapter,
) {
    internal fun getId(dao: ReservationDao): ReservationId = ReservationId(dao.getDaoId().value, this::getDetail)

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

    @Transactional
    suspend fun create(newReservation: NewReservation): Reservation =
        reservationRepository.save(
            ReservationDao(
                ownerId = newReservation.ownerId.value,
                sportsFieldId = newReservation.sportsFieldId.value,
                startTime = newReservation.timeSlot.startTime.value,
                endTime = newReservation.timeSlot.endTime.value,
                userNote = newReservation.userNote?.value,
                fieldManagerNote = newReservation.fieldManagerNote?.value,
            ),
        ).let { dao ->
            dao.toDomain(
                id = getId(dao),
                ownerId = userDbAdapter.getId(dao.getOwnerDaoId()),
                sportsFieldId = sportsFieldDbAdapter.getId(dao.getSportsFieldDaoId()),
            )
        }

    suspend fun isAlreadyReserved(
        sportsFieldId: SportsFieldId,
        timeSlot: TimeSlot,
    ): Boolean = reservationRepository.findBySportsFieldIdAndStartTimeAndEndTime(sportsFieldId.value, timeSlot.startTime.value, timeSlot.endTime.value) != null
}