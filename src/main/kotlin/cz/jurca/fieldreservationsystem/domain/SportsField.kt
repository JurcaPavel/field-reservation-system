package cz.jurca.fieldreservationsystem.domain

// TODO pricing
// TODO LATER INDOOR/OUTDOOR SPORTS FIELD AND MAKE THIS SEALED CLASS
// TODO later partial field
class SportsField(
    val id: SportsFieldId,
    val name: Name,
    val sportTypes: List<SportType>,
    val description: Description?,
    val address: Address,
    val coordinates: Coordinates,
    val managerId: UserId,
)