package cz.jurca.fieldreservationsystem.platform.secret

interface SecretProvider {
    fun getDatabaseCredentials(): DatabaseCredentials

    fun getRedisCredentials(): RedisCredentials

    class Secret(val value: String)

    class DatabaseCredentials(
        val host: Secret,
        val port: Secret,
        val database: Secret,
        val username: Secret,
        val password: Secret,
    )

    class RedisCredentials(
        val host: Secret,
        val port: Secret,
    )
}