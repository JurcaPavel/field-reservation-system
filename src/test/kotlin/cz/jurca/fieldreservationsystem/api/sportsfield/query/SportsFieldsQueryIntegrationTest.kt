package cz.jurca.fieldreservationsystem.api.sportsfield.query

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldsWrapper
import cz.jurca.fieldreservationsystem.repository.SportsFieldDao
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class SportsFieldsQueryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given some sports fields in db when get then return them`() {
        runBlocking {
            Given()
            // TODO test data builder
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
            sportsFieldRepository.save(
                SportsFieldDao(
                    name = "Field 3",
                    latitude = 50.0755,
                    longitude = 14.4378,
                    city = "Prague",
                    street = "Hlavní 3",
                    zipCode = "11000",
                    countryCode = "CZE",
                    description = "Test field 3",
                ),
            )
            val paginationInput = PaginationInput(1, 2)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryPaged(paginationInput),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields shouldHaveSize 2
            response.paginationInfo.itemsTotalCount shouldBe 3
        }
    }
    // TODO no sports fields in db
    // TODO high page should return no fields
    // TODO big page size and just one field in db should return just that one field
    // TODO filtering by country/city/sporttypes

    private fun sportsFieldsQueryPaged(paginationInput: PaginationInput): String =
        """
        query MyQuery {
          sportsFields(pagination: {pageNumber: ${paginationInput.pageNumber}, pageSize: ${paginationInput.pageSize}}) {
            paginationInfo {
              itemsTotalCount
            }
            sportsFields {
              city
              description
              id
              name
              sportTypes
              street
              zipCode
              country {
                code
                name
              }
              coordinates {
                latitude
                longitude
              }
            }
          }
        }
        """.trimIndent()
}