package cz.jurca.fieldreservationsystem.db.adapter

import cz.jurca.fieldreservationsystem.domain.SportsFieldId

// Workaround for transactional boundaries.
interface SportsFieldDeletable {
    suspend fun delete(sportsFieldId: SportsFieldId)
}