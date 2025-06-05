package cz.jurca.fieldreservationsystem.repository

import cz.jurca.fieldreservationsystem.domain.SportType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportTypeRepository : CoroutineCrudRepository<SportTypeDao, Int>

@Table("sport_type")
data class SportTypeDao(
    val name: String,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): Int = id!!

    fun toDomain() = SportType.valueOf(name)
}