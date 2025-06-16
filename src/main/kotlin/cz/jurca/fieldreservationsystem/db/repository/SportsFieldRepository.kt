package cz.jurca.fieldreservationsystem.db.repository

import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Coordinates
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Latitude
import cz.jurca.fieldreservationsystem.domain.Longitude
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsField
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.UnvalidatedSportsFieldId
import cz.jurca.fieldreservationsystem.domain.User
import cz.jurca.fieldreservationsystem.domain.UserId
import cz.jurca.fieldreservationsystem.domain.ZipCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldRepository : CoroutineCrudRepository<SportsFieldDao, Int>

@Table("sports_field")
data class SportsFieldDao(
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var city: String,
    var street: String,
    var zipCode: String,
    var countryCode: String,
    var description: String?,
    val managerId: Int,
) {
    @Id
    private var id: Int? = null

    fun getDaoId(): SportsFieldDaoId = SportsFieldDaoId(id!!)

    suspend fun toDomain(
        idProvider: suspend (UnvalidatedSportsFieldId) -> SportsFieldId?,
        userDetailProvider: suspend (UserId) -> User,
        sportTypes: List<SportType>,
    ): SportsField {
        return SportsField(
            id = requireNotNull(UnvalidatedSportsFieldId(getDaoId().value).validate(idProvider).getOrNull()) { "This should never happen as the id comes from db already. " },
            name = Name(name),
            address =
                Address(
                    city = City(city),
                    street = Street(street),
                    zipCode = ZipCode(zipCode),
                    country = requireNotNull(Country.findByCode(Country.AlphaCode3(countryCode))),
                ),
            coordinates = Coordinates(Latitude(latitude), Longitude(longitude)),
            description = description?.let { Description(it) },
            sportTypes = sportTypes,
            managerId = UserId(managerId, userDetailProvider),
        )
    }
}

data class SportsFieldDaoId(val value: Int)