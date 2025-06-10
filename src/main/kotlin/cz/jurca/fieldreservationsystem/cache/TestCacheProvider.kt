package cz.jurca.fieldreservationsystem.cache

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

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
    ): T = value
}