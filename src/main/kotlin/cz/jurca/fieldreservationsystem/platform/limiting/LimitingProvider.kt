package cz.jurca.fieldreservationsystem.platform.limiting

import cz.jurca.fieldreservationsystem.domain.Username

interface LimitingProvider {
    suspend fun shouldLimitAuthentication(username: Username): Boolean
}