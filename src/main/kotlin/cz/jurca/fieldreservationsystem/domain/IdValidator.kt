package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.db.adapter.ReservationDbAdapter
import cz.jurca.fieldreservationsystem.db.adapter.SportsFieldDbAdapter
import cz.jurca.fieldreservationsystem.db.adapter.UserDbAdapter
import org.springframework.stereotype.Component

@Component
class IdValidator(
    private val sportsFieldDbAdapter: SportsFieldDbAdapter,
    private val userDbAdapter: UserDbAdapter,
    private val reservationDbAdapter: ReservationDbAdapter,
) {
    suspend fun existsSportsField(id: Int): SportsFieldValidationResult = UnvalidatedSportsFieldId(id).validate(sportsFieldDbAdapter::findSportsField).getOrElse { SportFieldNotFound }

    suspend fun existsUser(id: Int): UserValidationResult = UnvalidatedUserId(id).validate(userDbAdapter::findUser).getOrElse { UserNotFound }

    suspend fun existsReservation(id: Int): ReservationValidationResult = UnvalidatedReservationId(id).validate(reservationDbAdapter::findReservation).getOrElse { ReservationNotFound }
}