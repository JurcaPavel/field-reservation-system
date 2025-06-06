package cz.jurca.fieldreservationsystem.repository

import cz.jurca.fieldreservationsystem.domain.Address
import cz.jurca.fieldreservationsystem.domain.City
import cz.jurca.fieldreservationsystem.domain.Country
import cz.jurca.fieldreservationsystem.domain.Description
import cz.jurca.fieldreservationsystem.domain.Name
import cz.jurca.fieldreservationsystem.domain.SportType
import cz.jurca.fieldreservationsystem.domain.SportsField
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.Street
import cz.jurca.fieldreservationsystem.domain.ZipCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SportsFieldRepository : CoroutineCrudRepository<SportsFieldDao, Int>

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
    @Id
    private var id: Int? = null

    fun getDaoId(): Int = id!!

    fun toDomain(
        detailProvider: suspend (SportsFieldId) -> SportsField,
        sportTypes: List<SportType>,
    ): SportsField {
        return SportsField(
            id = SportsFieldId(getDaoId(), detailProvider),
            name = Name(name),
            address =
                Address(
                    city = City(city),
                    street = Street(street),
                    zipCode = ZipCode(zipCode),
                    country = requireNotNull(Country.findByCode(Country.AlphaCode3(countryCode))),
                ),
            latitude = latitude,
            longitude = longitude,
            description = description?.let { Description(it) },
            sportTypes = sportTypes,
        )
    }
}