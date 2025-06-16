package cz.jurca.fieldreservationsystem.platform.secret

import cz.jurca.fieldreservationsystem.platform.secret.SecretProvider.DatabaseCredentials
import cz.jurca.fieldreservationsystem.platform.secret.SecretProvider.RedisCredentials
import cz.jurca.fieldreservationsystem.platform.secret.SecretProvider.Secret
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Primary
@Component
@Profile("local")
class LocalSecretProvider : SecretProvider {
    override fun getDatabaseCredentials(): DatabaseCredentials =
        DatabaseCredentials(
            host = Secret(System.getenv("DB_HOST") ?: "localhost"),
            port = Secret(System.getenv("DB_PORT") ?: "54328"),
            database = Secret(System.getenv("DB_NAME") ?: "field_reservation"),
            username = Secret(System.getenv("DB_USER") ?: "field_reservation_db_user"),
            password = Secret(System.getenv("DB_PASSWORD") ?: "field_reservation_db_password"),
        )

    override fun getRedisCredentials(): RedisCredentials =
        RedisCredentials(
            host = Secret(System.getenv("REDIS_HOST") ?: "localhost"),
            port = Secret(System.getenv("REDIS_PORT") ?: "6379"),
        )
}