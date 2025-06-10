package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock

class NewSportsFieldTest : BaseTest() {
    @Test
    fun `given user is not manager or admin when create new sports field then return NotManagerOrAdminError`() =
        runBlocking {
            Given()
            val newSportsField = createNewSportsField(UserRole.BASIC)

            When()
            val result = newSportsField.create()

            Then()
            result.shouldBeLeft().run {
                message shouldBe "Only field manager or admin can create sports field"
            }
        }

    @Test
    fun `given user is manager when create new sports field then return SportsField`() =
        runBlocking {
            Given()
            val newSportsField = createNewSportsField(UserRole.MANAGER)

            When()

            val result = newSportsField.create()

            Then()
            result.shouldBeRight()
        }

    @Test
    fun `given user is admin when create new sports field then return SportsField`() =
        runBlocking {
            Given()
            val newSportsField = createNewSportsField(UserRole.ADMIN)

            When()
            val result = newSportsField.create()
            Then()
            result.shouldBeRight()
        }

    private fun createNewSportsField(userRole: UserRole): NewSportsField =
        NewSportsField(
            name = Name("Test Field"),
            coordinates = Coordinates(Latitude(48.1486), Longitude(17.1077)),
            address =
                Address(
                    city = City("Bratislava"),
                    street = Street("Main Street"),
                    zipCode = ZipCode("81101"),
                    country = Country.SLOVAKIA,
                ),
            description = Description("Test description"),
            sportTypes = listOf(SportType.SOCCER),
            loginUser = LoginUser(1, Username("username"), userRole),
            createSportsFieldProvider = mock(),
        )
}