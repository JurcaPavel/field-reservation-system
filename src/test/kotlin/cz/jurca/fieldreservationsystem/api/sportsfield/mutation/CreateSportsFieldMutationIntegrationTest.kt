package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.CoordinatesInput
import cz.jurca.fieldreservationsystem.codegen.types.CreateSportsFieldInput
import cz.jurca.fieldreservationsystem.codegen.types.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.codegen.types.SportType
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking

class CreateSportsFieldMutationIntegrationTest : BaseIntegrationTest() {
    @Test
    fun ` when create sports field then return created Sports field`() =
        runBlocking {
            Given()
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)
            val input =
                CreateSportsFieldInput(
                    name = "New Sports Field",
                    coordinates = CoordinatesInput(latitude = 48.1486, longitude = 17.1077),
                    city = "New York",
                    street = "5th Avenue",
                    zipCode = "10001",
                    countryCode = "USA",
                    sportTypes = listOf(SportType.SOCCER, SportType.BASKETBALL),
                    description = "A new sports field for testing.",
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    createSportsFieldMutationRequest(input),
                    "data.createSportsField",
                    SportsField::class.java,
                )

            Then()
            response.run {
                id shouldNotBe null
                name shouldBe "New Sports Field"
                sportTypes shouldBe listOf(SportType.SOCCER, SportType.BASKETBALL)
                coordinates.latitude shouldBe 48.1486
                coordinates.longitude shouldBe 17.1077
                city shouldBe "New York"
                street shouldBe "5th Avenue"
                zipCode shouldBe "10001"
                country.code shouldBe "USA"
                country.name shouldBe "United States of America (the)"
                description shouldBe "A new sports field for testing."
            }
        }

    @Test
    fun `given user not manager or admin when create sports field then return NotManagerOrAdminError`() =
        runBlocking {
            Given()
            val basicUserDao = dataBuilder.buildUser(username = "pjb", email = "basic@email.com", role = "BASIC")
            setUserInTestSecurityContextHolder(basicUserDao)
            val input =
                CreateSportsFieldInput(
                    name = "New Sports Field",
                    coordinates = CoordinatesInput(latitude = 48.1486, longitude = 17.1077),
                    city = "New York",
                    street = "5th Avenue",
                    zipCode = "10001",
                    countryCode = "USA",
                    sportTypes = listOf(SportType.SOCCER, SportType.BASKETBALL),
                    description = "A new sports field for testing.",
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    createSportsFieldMutationRequest(input),
                    "data.createSportsField",
                    NotManagerOrAdminError::class.java,
                )

            Then()
            response.message shouldBe "Only field manager or admin can create sports field"
        }

    private val createSportsFieldMutationRequest: (createSportsFieldInput: CreateSportsFieldInput) -> String = { input ->
        DgsClient.buildMutation {
            createSportsField(input) {
                onSportsField {
                    id
                    name
                    sportTypes
                    coordinates {
                        latitude
                        longitude
                    }
                    city
                    street
                    zipCode
                    country {
                        code
                        name
                    }
                    description
                }
                onNotManagerOrAdminError {
                    message
                }
            }
        }
    }
}