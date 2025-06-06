package cz.jurca.fieldreservationsystem.repository

import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldRepository : CoroutineCrudRepository<SportsFieldDao, Int> {
    @Query("SELECT * FROM sports_field LIMIT :pageSize OFFSET :offset")
    suspend fun findAllPaged(pageSize: Int, offset: Int): List<SportsFieldDao>
}

@Table("sports_field")
data class SportsFieldDao(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val street: String,
    val zipCode: String,
    val countryCode: String,
    val description: String?,
) {
    @Id private var id: Int? = null

    fun getDaoId(): Int = id!!
}