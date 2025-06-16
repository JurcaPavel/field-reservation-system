package cz.jurca.fieldreservationsystem.platform.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
@Profile("!test")
class RedisCacheProvider(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CacheProvider {
    private val logger = KotlinLogging.logger { }

    override suspend fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T? {
        val redisResponse =
            reactiveRedisTemplate.opsForValue().get(key)
                .onErrorResume { e ->
                    logger.error(e) { "Redis get operation failed for key: $key" }
                    Mono.empty()
                }
                .awaitFirstOrNull()
        return redisResponse?.let { objectMapper.readValue(it, clazz) }
    }

    override suspend fun <T> put(
        key: String,
        value: T,
        ttl: Duration,
    ): T {
        val json = objectMapper.writeValueAsString(value)
        reactiveRedisTemplate.opsForValue().set(key, json, ttl)
            .onErrorResume { e ->
                logger.error(e) { "Redis set operation failed for key: $key" }
                Mono.empty()
            }
            .awaitFirstOrNull()
        return value
    }

    override suspend fun evict(key: String): Boolean =
        reactiveRedisTemplate.opsForValue().delete(key)
            .onErrorResume { e ->
                logger.error(e) { "Redis delete operation failed for key: $key" }
                Mono.just(false)
            }
            .awaitFirst()
}