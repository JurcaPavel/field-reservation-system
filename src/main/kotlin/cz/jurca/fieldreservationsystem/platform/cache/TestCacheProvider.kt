package cz.jurca.fieldreservationsystem.platform.cache

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("test")
class TestCacheProvider : CacheProvider {
    override suspend fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T? = null

    override suspend fun <T> put(
        key: String,
        value: T,
        ttl: Duration,
    ): T = value

    override suspend fun evict(key: String): Boolean = true
}