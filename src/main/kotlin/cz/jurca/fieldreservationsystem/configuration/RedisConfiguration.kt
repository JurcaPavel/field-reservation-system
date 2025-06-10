package cz.jurca.fieldreservationsystem.configuration

import cz.jurca.fieldreservationsystem.secret.SecretProvider
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
@Profile("!test")
class RedisConfiguration(
    secretProvider: SecretProvider,
) {
    val credentials = secretProvider.getRedisCredentials()

    @Bean
    fun redisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(
            RedisStandaloneConfiguration(
                credentials.host.value,
                credentials.port.value.toInt(),
            ).apply {
                database = 0
            },
        )
    }

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        val serializationContext =
            RedisSerializationContext
                .newSerializationContext<String, String>(StringRedisSerializer())
                .value(StringRedisSerializer())
                .build()
        return ReactiveRedisTemplate(factory, serializationContext)
    }
}