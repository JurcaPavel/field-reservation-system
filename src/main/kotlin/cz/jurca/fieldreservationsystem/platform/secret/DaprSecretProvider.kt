package cz.jurca.fieldreservationsystem.platform.secret

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test & !local")
class DaprSecretProvider : SecretProvider {
    override fun getDatabaseCredentials(): SecretProvider.DatabaseCredentials {
        TODO("Not yet implemented")
    }

    override fun getRedisCredentials(): SecretProvider.RedisCredentials {
        TODO("Not yet implemented")
    }
}