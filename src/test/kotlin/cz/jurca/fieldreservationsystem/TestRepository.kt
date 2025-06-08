package cz.jurca.fieldreservationsystem

import cz.jurca.fieldreservationsystem.repository.SportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class TestRepository(
    private val sportTypeRepository: SportTypeRepository,
    private val sportsFieldRepository: SportsFieldRepository,
    private val sportsFieldSportTypeRepository: SportsFieldSportTypeRepository,
) {
    fun deleteAll() =
        runBlocking {
            sportsFieldSportTypeRepository.deleteAll()
            sportTypeRepository.deleteAll()
            sportsFieldRepository.deleteAll()
        }

    fun saveSportsField(sportsFieldDao: SportsFieldDao) =
        runBlocking {
            sportsFieldRepository.save(sportsFieldDao)
        }

    fun saveSportType(name: String): SportTypeDao =
        runBlocking {
            sportTypeRepository.save(SportTypeDao(name))
        }

    fun saveSportsFieldSportsType(sportsFieldSportTypeDao: SportsFieldSportTypeDao) =
        runBlocking {
            sportsFieldSportTypeRepository.save(sportsFieldSportTypeDao)
        }

    fun findSportTypeByName(name: String): SportTypeDao =
        runBlocking {
            sportTypeRepository.findByName(name)
                ?: throw IllegalArgumentException("Sport type with name '$name' not found")
        }
}