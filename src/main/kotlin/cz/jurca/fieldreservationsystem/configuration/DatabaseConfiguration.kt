package cz.jurca.fieldreservationsystem.configuration

import cz.jurca.fieldreservationsystem.secret.SecretProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.CustomConversions
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

@Configuration
@EnableR2dbcRepositories
@Profile("!test")
class DatabaseConfiguration(
    secretProvider: SecretProvider,
) : AbstractR2dbcConfiguration() {
    val credentials = secretProvider.getDatabaseCredentials()

    @Bean
    override fun connectionFactory(): ConnectionFactory =
        credentials.run {
            PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration
                    .builder()
                    .host(host.value)
                    .port(port.value.toInt())
                    .database(database.value)
                    .username(username.value)
                    .password(password.value)
                    .build(),
            )
        }

    @ReadingConverter
    class LocalDateTimeToOffsetDateTimeReadingConverter : Converter<LocalDateTime, OffsetDateTime> {
        private val logger = KotlinLogging.logger {}
        private val zoneId: ZoneId = ZoneId.systemDefault()

        override fun convert(source: LocalDateTime): OffsetDateTime {
            logger.debug { "convert() called with: source = $source" }
            return source.atZone(zoneId).toOffsetDateTime()
        }
    }

    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory)
        val converters: MutableList<Any> = ArrayList(dialect.converters)
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS)
        val storeConversions = CustomConversions.StoreConversions.of(dialect.simpleTypeHolder, converters)
        val converterList: List<Converter<*, *>> =
            listOf(
                LocalDateTimeToOffsetDateTimeReadingConverter(),
            )
        return R2dbcCustomConversions(storeConversions, converterList)
    }

    @Bean(initMethod = "migrate")
    fun flyway(connectionFactory: ConnectionFactory): Flyway =
        credentials.run {
            Flyway(
                Flyway
                    .configure()
                    .baselineOnMigrate(true)
                    .dataSource(
                        "jdbc:postgresql://${host.value}:${port.value}/${database.value}",
                        username.value,
                        password.value,
                    ),
            )
        }
}