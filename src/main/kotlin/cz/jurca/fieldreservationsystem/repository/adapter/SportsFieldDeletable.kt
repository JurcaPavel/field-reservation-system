package cz.jurca.fieldreservationsystem.repository.adapter

import cz.jurca.fieldreservationsystem.domain.SportsFieldId

// Workaround for transactional boundaries.
interface SportsFieldDeletable {
    suspend fun delete(sportsFieldId: SportsFieldId)
}