package cz.jurca.fieldreservationsystem.db.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldSportTypeRepository : CoroutineCrudRepository<SportsFieldSportTypeDao, Int> {
    suspend fun deleteAllBySportsFieldId(sportsFieldId: Int)
}

@Table("sports_field_sport_type")
data class SportsFieldSportTypeDao(
    val sportsFieldId: Int,
    val sportTypeId: Int,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): SportsFieldSportTypeDaoId = SportsFieldSportTypeDaoId(id!!)
}

data class SportsFieldSportTypeDaoId(val value: Int)