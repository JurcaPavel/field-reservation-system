package cz.jurca.field_reservation_system.secret

import cz.jurca.field_reservation_system.secret.SecretProvider.*
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Primary
@Component
@Profile("local")
class LocalSecretProvider : SecretProvider {
    override fun getDatabaseCredentials(): DatabaseCredentials =
        DatabaseCredentials(
            host = Secret("localhost"),
            port = Secret("5432"),
            database = Secret("reservation_system"),
            username = Secret("reservation_systemdbuser"),
            password = Secret("reservation_systemservicepassword@666"),
        )
}