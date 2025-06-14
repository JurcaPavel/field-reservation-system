package cz.jurca.fieldreservationsystem.cache

import java.time.Duration

interface CacheProvider {
    suspend fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T?

    suspend fun <T> put(
        key: String,
        value: T,
        // ttl Duration.ZERO means no expiration
        ttl: Duration = Duration.ZERO,
    ): T

    suspend fun evict(
        key: String,
    ): Boolean
}