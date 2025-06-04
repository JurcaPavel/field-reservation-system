package cz.jurca.field_reservation_system.secret

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test & !local")
class DaprSecretProvider : SecretProvider {
    override fun getDatabaseCredentials(): SecretProvider.DatabaseCredentials {
        TODO("Not yet implemented")
    }
}