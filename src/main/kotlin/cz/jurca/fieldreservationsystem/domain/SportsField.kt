package cz.jurca.fieldreservationsystem.domain

// TODO LATER INDOOR/OUTDOOR SPORTS FIELD AND MAKE THIS SEALED CLASS
class SportsField(
    val id: SportsFieldId,
    val name: Name,
    val sportTypes: List<SportType>,
    val description: Description?,
    val address: Address,
    val latitude: Double,
    val longitude: Double,
)