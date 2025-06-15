package cz.jurca.fieldreservationsystem.repository

import cz.jurca.fieldreservationsystem.domain.DateTime
import cz.jurca.fieldreservationsystem.domain.Note
import cz.jurca.fieldreservationsystem.domain.Reservation
import cz.jurca.fieldreservationsystem.domain.ReservationId
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.TimeSlot
import cz.jurca.fieldreservationsystem.domain.UserId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReservationRepository : CoroutineCrudRepository<ReservationDao, Int> {
    suspend fun findBySportsFieldIdAndStartTimeAndEndTime(
        sportsFieldId: Int,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime,
    ): ReservationDao?
}

@Table("reservation")
data class ReservationDao(
    val ownerId: Int,
    val sportsFieldId: Int,
    var startTime: OffsetDateTime,
    var endTime: OffsetDateTime,
    var userNote: String?,
    var fieldManagerNote: String?,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): ReservationDaoId = ReservationDaoId(id!!)

    fun getSportsFieldDaoId(): SportsFieldDaoId = SportsFieldDaoId(sportsFieldId)

    fun getOwnerDaoId(): UserDaoId = UserDaoId(ownerId)

    fun toDomain(
        id: ReservationId,
        ownerId: UserId,
        sportsFieldId: SportsFieldId,
    ): Reservation =
        Reservation(
            id = id,
            ownerId = ownerId,
            sportsFieldId = sportsFieldId,
            timeSlot =
                TimeSlot(
                    startTime = DateTime(startTime),
                    endTime = DateTime(endTime),
                ),
            userNote = userNote?.let { Note(it) },
            fieldManagerNote = fieldManagerNote?.let { Note(it) },
        )
}

class ReservationDaoId(val value: Int)