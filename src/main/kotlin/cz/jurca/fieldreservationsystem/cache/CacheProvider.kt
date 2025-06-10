package cz.jurca.fieldreservationsystem.cache

interface CacheProvider {
    suspend fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T?

    suspend fun <T> put(
        key: String,
        value: T,
    ): T
}