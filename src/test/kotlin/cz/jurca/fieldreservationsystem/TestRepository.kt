package cz.jurca.fieldreservationsystem

import cz.jurca.fieldreservationsystem.repository.ReservationDao
import cz.jurca.fieldreservationsystem.repository.ReservationRepository
import cz.jurca.fieldreservationsystem.repository.SportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeDao
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import cz.jurca.fieldreservationsystem.repository.UserDao
import cz.jurca.fieldreservationsystem.repository.UserRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class TestRepository(
    private val sportTypeRepository: SportTypeRepository,
    private val sportsFieldRepository: SportsFieldRepository,
    private val sportsFieldSportTypeRepository: SportsFieldSportTypeRepository,
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository,
) {
    fun deleteAll() =
        runBlocking {
            reservationRepository.deleteAll()
            sportsFieldSportTypeRepository.deleteAll()
            sportTypeRepository.deleteAll()
            sportsFieldRepository.deleteAll()
            userRepository.deleteAll()
        }

    fun saveUser(userDao: UserDao) =
        runBlocking {
            userRepository.save(userDao)
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

    fun saveReservation(reservationDao: ReservationDao) =
        runBlocking {
            reservationRepository.save(reservationDao)
        }

    fun findSportTypeByName(name: String): SportTypeDao =
        runBlocking {
            sportTypeRepository.findByName(name)
                ?: throw IllegalArgumentException("Sport type with name '$name' not found")
        }

    fun findAllSportFields(): List<SportsFieldDao> =
        runBlocking {
            sportsFieldRepository.findAll().toList()
        }

    fun findAllSportFieldSportTypes(): List<SportsFieldSportTypeDao> =
        runBlocking {
            sportsFieldSportTypeRepository.findAll().toList()
        }

    fun findAllReservations(): List<ReservationDao> =
        runBlocking {
            reservationRepository.findAll().toList()
        }
}