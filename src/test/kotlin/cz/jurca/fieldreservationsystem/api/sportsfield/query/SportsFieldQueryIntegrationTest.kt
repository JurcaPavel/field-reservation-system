package cz.jurca.fieldreservationsystem.api.sportsfield.query

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.domain.SportType
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class SportsFieldQueryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given some sports fields in db when get then return the correct one`() =
        runBlocking {
            Given()
            dataBuilder.buildSportsField(
                name = "Field 1",
                latitude = 48.1486,
                longitude = 17.1077,
                city = "Bratislava",
                street = "Hlavná 1",
                zipCode = "81101",
                countryCode = "SVK",
                description = "Test field 1",
            )
            val testedDao =
                dataBuilder.buildSportsField(
                    name = "Field 2",
                    latitude = 48.2089,
                    longitude = 16.3726,
                    city = "Vienna",
                    street = "Hauptstrasse 2",
                    zipCode = "1010",
                    countryCode = "AUT",
                    description = "Test field 2",
                    sportTypes = listOf(SportType.TENNIS),
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldQueryRequest(testedDao.getDaoId().value),
                    "data.sportsField",
                    SportsField::class.java,
                )

            Then()
            response.run {
                id shouldBe testedDao.getDaoId().value.toString()
                name shouldBe "Field 2"
                coordinates.latitude shouldBe 48.2089
                coordinates.longitude shouldBe 16.3726
                city shouldBe "Vienna"
                street shouldBe "Hauptstrasse 2"
                zipCode shouldBe "1010"
                country.code shouldBe "AUT"
                country.name shouldBe "Austria"
                description shouldBe "Test field 2"
                sportTypes shouldBe listOf(cz.jurca.fieldreservationsystem.codegen.types.SportType.TENNIS)
            }
        }

    @Test
    fun `given some sports field in db when get by nonexisting id then return error`() =
        runBlocking {
            Given()
            dataBuilder.buildSportsField(
                name = "Field 1",
                latitude = 48.1486,
                longitude = 17.1077,
                city = "Bratislava",
                street = "Hlavná 1",
                zipCode = "81101",
                countryCode = "SVK",
                description = "Test field 1",
            )

            When()
            val result =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldQueryRequest(666666),
                    "data.sportsField",
                    NotFoundError::class.java,
                )

            Then()
            result.message shouldBe "Sports field with id 666666 not found"
        }

    private val sportsFieldQueryRequest: (id: Int) -> String = { requestedId ->
        DgsClient.buildQuery {
            sportsField(requestedId.toString()) {
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
            }
        }
    }
}