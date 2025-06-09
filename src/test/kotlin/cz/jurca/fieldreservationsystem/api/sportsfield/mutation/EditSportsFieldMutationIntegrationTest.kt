package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.CoordinatesInput
import cz.jurca.fieldreservationsystem.codegen.types.EditSportsFieldInput
import cz.jurca.fieldreservationsystem.codegen.types.SportType
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.domain.NotResourceOwnerError
import io.kotest.common.runBlocking
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class EditSportsFieldMutationIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given sports field exists when edit sports field with correct manager then return edited sports field`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            val editInput = EditSportsFieldInput(
                name = "Updated Sports Field",
                coordinates = CoordinatesInput(latitude = 48.1486, longitude = 17.1077),
                city = "New York",
                street = "5th Avenue",
                zipCode = "10001",
                countryCode = "USA",
                sportTypes = listOf(SportType.SOCCER, SportType.BASKETBALL),
                description = "Updated sports field for testing.",
            )
            setUserInTestSecurityContextHolder(dataBuilder.defaultManager)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    editSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString(), editInput),
                    "data.editSportsField",
                    SportsField::class.java,
                )

            Then()
            response.run {
                id shouldBe sportsFieldDao.getDaoId().toString()
                name shouldBe "Updated Sports Field"
                sportTypes shouldContainExactlyInAnyOrder listOf(SportType.SOCCER, SportType.BASKETBALL)
                coordinates.latitude shouldBe 48.1486
                coordinates.longitude shouldBe 17.1077
                city shouldBe "New York"
                street shouldBe "5th Avenue"
                zipCode shouldBe "10001"
                country.code shouldBe "USA"
                country.name shouldBe "United States of America (the)"
                description shouldBe "Updated sports field for testing."
            }
        }

    @Test
    fun `given sports field exists when edit sports field with admin then return edited sports field`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            val editInput = EditSportsFieldInput(
                name = "Updated Sports Field",
                coordinates = CoordinatesInput(latitude = 48.1486, longitude = 17.1077),
                city = "New York",
                street = "5th Avenue",
                zipCode = "10001",
                countryCode = "USA",
                sportTypes = listOf(SportType.SOCCER, SportType.BASKETBALL),
                description = "Updated sports field for testing.",
            )
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    editSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString(), editInput),
                    "data.editSportsField",
                    SportsField::class.java,
                )

            Then()
            response.run {
                id shouldBe sportsFieldDao.getDaoId().toString()
                name shouldBe "Updated Sports Field"
                sportTypes shouldContainExactlyInAnyOrder listOf(SportType.SOCCER, SportType.BASKETBALL)
                coordinates.latitude shouldBe 48.1486
                coordinates.longitude shouldBe 17.1077
                city shouldBe "New York"
                street shouldBe "5th Avenue"
                zipCode shouldBe "10001"
                country.code shouldBe "USA"
                country.name shouldBe "United States of America (the)"
                description shouldBe "Updated sports field for testing."
            }
        }

    @Test
    fun `given sports field exists when edit sports field with different manager then return NotResourceOwnerError`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            val editInput = EditSportsFieldInput(
                name = "Updated Sports Field",
                coordinates = CoordinatesInput(latitude = 48.1486, longitude = 17.1077),
                city = "New York",
                street = "5th Avenue",
                zipCode = "10001",
                countryCode = "USA",
                sportTypes = listOf(SportType.SOCCER, SportType.BASKETBALL),
                description = "Updated sports field for testing.",
            )
            val managerDao = dataBuilder.buildUser(username = "pjm2", email = "pjm2@email.com", role = "MANAGER")
            setUserInTestSecurityContextHolder(managerDao)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    editSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString(), editInput),
                    "data.editSportsField",
                    NotResourceOwnerError::class.java,
                )

            Then()
            response.run {
                message shouldBe "User pjm2 cannot edit sports field with id ${sportsFieldDao.getDaoId()} because he is not manager of this field."
            }
        }

    private val editSportsFieldMutationRequest: (id: String, editSportsFieldInput: EditSportsFieldInput) -> String = { fieldId, editInput ->
        DgsClient.buildMutation {
            editSportsField(fieldId, editInput) {
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
                onNotFoundError { message }
                onNotResourceOwnerError { message }
            }
        }
    }
}