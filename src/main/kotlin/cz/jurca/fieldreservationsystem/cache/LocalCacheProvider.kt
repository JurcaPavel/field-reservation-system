package cz.jurca.fieldreservationsystem.cache

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Primary
@Component
@Profile("local")
class LocalCacheProvider(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CacheProvider {
    override suspend fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T? {
        val redisResponse = reactiveRedisTemplate.opsForValue().get(key).awaitFirstOrNull()
        return redisResponse?.let { objectMapper.readValue(it, clazz) }
    }

    override suspend fun <T> put(
        key: String,
        value: T,
    ): T {
        val json = objectMapper.writeValueAsString(value)
        reactiveRedisTemplate.opsForValue().set(key, json).awaitFirst()
        return value
    }
}