package cz.jurca.fieldreservationsystem.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldRepository : CoroutineCrudRepository<SportsFieldDao, Int>

@Table("sports_field")
data class SportsFieldDao(
    val name: String,
) {
    @Id private var id: Int? = null
}