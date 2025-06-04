package cz.jurca.fieldreservationsystem.secret

interface SecretProvider {
    fun getDatabaseCredentials(): DatabaseCredentials

    class Secret(val value: String)

    class DatabaseCredentials(
        val host: Secret,
        val port: Secret,
        val database: Secret,
        val username: Secret,
        val password: Secret,
    )
}