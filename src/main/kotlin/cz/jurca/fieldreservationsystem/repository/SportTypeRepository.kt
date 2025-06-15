package cz.jurca.fieldreservationsystem.repository

import cz.jurca.fieldreservationsystem.domain.SportType
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportTypeRepository : CoroutineCrudRepository<SportTypeDao, Int> {
    @Query(
        """
        SELECT st.*
        FROM sport_type st
        JOIN sports_field_sport_type sfst ON st.id = sfst.sport_type_id
        WHERE sfst.sports_field_id = :sportsFieldId
        """,
    )
    suspend fun findAllBySportsFieldId(sportsFieldId: Int): List<SportTypeDao>

    suspend fun findAllByNameIn(names: List<String>): List<SportTypeDao>

    suspend fun findByName(name: String): SportTypeDao?
}

@Table("sport_type")
data class SportTypeDao(
    val name: String,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): SportTypeDaoId = SportTypeDaoId(id!!)

    fun toDomain() = SportType.valueOf(name)
}

data class SportTypeDaoId(val value: Int)