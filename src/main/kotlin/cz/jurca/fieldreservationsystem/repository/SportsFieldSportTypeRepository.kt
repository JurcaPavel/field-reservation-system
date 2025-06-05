package cz.jurca.fieldreservationsystem.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldSportTypeRepository : CoroutineCrudRepository<SportsFieldSportTypeDao, Int> {
    suspend fun findBySportsFieldId(sportsFieldId: Int): List<SportsFieldSportTypeDao>
}

@Table("sports_field_sport_type")
data class SportsFieldSportTypeDao(
    val sportsFieldId: Int,
    val sportTypeId: Int,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): Int = id!!
}