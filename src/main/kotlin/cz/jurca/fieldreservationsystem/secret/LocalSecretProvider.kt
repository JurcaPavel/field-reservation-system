package cz.jurca.fieldreservationsystem.secret

import cz.jurca.fieldreservationsystem.secret.SecretProvider.DatabaseCredentials
import cz.jurca.fieldreservationsystem.secret.SecretProvider.Secret
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
            port = Secret("54328"),
            database = Secret("field_reservation"),
            username = Secret("field_reservation_db_user"),
            password = Secret("field_reservation_db_password"),
        )
}