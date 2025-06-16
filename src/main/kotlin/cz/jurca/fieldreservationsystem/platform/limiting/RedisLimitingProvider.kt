package cz.jurca.fieldreservationsystem.platform.limiting

import cz.jurca.fieldreservationsystem.domain.Username
import cz.jurca.fieldreservationsystem.platform.cache.CacheProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("!test")
class RedisLimitingProvider(
    private val cacheProvider: CacheProvider,
) : LimitingProvider {
    private val logger = KotlinLogging.logger { }
    private val maxAttempts = 3
    private val windowDuration = Duration.ofMinutes(1)

    override suspend fun shouldLimitAuthentication(username: Username): Boolean {
        val key = "auth-attempts:${username.value}"
        val attempts = cacheProvider.get(key, Int::class.java) ?: 0

        if (attempts >= maxAttempts) {
            logger.info { "Rate limiting authentication for user: ${username.value} (exceeded $maxAttempts attempts in ${windowDuration.toSeconds()} seconds)" }
            return true
        }

        cacheProvider.put(key, attempts + 1, windowDuration)

        return false
    }
}