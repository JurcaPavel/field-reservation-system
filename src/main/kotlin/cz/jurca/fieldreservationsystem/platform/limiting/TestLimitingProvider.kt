package cz.jurca.fieldreservationsystem.platform.limiting

import cz.jurca.fieldreservationsystem.domain.Username
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("test")
class TestLimitingProvider : LimitingProvider {
    override suspend fun shouldLimitAuthentication(username: Username): Boolean {
        return false
    }
}