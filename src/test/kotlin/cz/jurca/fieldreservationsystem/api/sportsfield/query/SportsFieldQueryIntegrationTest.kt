package cz.jurca.fieldreservationsystem.api.sportsfield.query

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class SportsFieldQueryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given some sports fields in db when get then return the correct one`() {
        runBlocking {
            Given()
            sportsFieldRepository.save(
                SportsFieldDao(
                    name = "Field 1",
                    latitude = 48.1486,
                    longitude = 17.1077,
                    city = "Bratislava",
                    street = "Hlavná 1",
                    zipCode = "81101",
                    countryCode = "SVK",
                    description = "Test field 1",
                ),
            )
            val testedDao =
                sportsFieldRepository.save(
                    SportsFieldDao(
                        name = "Field 2",
                        latitude = 48.2089,
                        longitude = 16.3726,
                        city = "Vienna",
                        street = "Hauptstrasse 2",
                        zipCode = "1010",
                        countryCode = "AUT",
                        description = "Test field 2",
                    ),
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldQuery(testedDao.getDaoId()),
                    "data.sportsField",
                    SportsField::class.java,
                )

            Then()
            response.run {
                name shouldBe "Field 2"
            }
        }
    }

    @Test
    fun `given some sports field in db when get by nonexisting id then return error`() {
        runBlocking {
            Given()
            sportsFieldRepository.save(
                SportsFieldDao(
                    name = "Field 1",
                    latitude = 48.1486,
                    longitude = 17.1077,
                    city = "Bratislava",
                    street = "Hlavná 1",
                    zipCode = "81101",
                    countryCode = "SVK",
                    description = "Test field 1",
                ),
            )

            When()
            val result =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldQuery(666666),
                    "data.sportsField",
                    NotFoundError::class.java,
                )

            Then()
            result.message shouldBe "Sports field with id 666666 not found"
        }
    }

    private fun sportsFieldQuery(id: Int) =
        """
        query {
            sportsField(id: "$id") {
                ... on SportsField {
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
                }
                ... on NotFoundError {
                    message
                }
            }
        }
        """.trimIndent()
}