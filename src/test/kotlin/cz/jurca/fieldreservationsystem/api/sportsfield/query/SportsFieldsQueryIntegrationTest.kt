package cz.jurca.fieldreservationsystem.api.sportsfield.query

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.PaginationInput
import cz.jurca.fieldreservationsystem.codegen.types.SortByDirection
import cz.jurca.fieldreservationsystem.codegen.types.SportType
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldFiltersInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldSortByField
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldSortByInput
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldsWrapper
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class SportsFieldsQueryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given no sports fields in db when get then return empty response`() {
        runBlocking {
            Given()
            val paginationInput = PaginationInput(1, 10)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, null, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields.shouldBeEmpty()
            response.paginationInfo.itemsTotalCount shouldBe 0
        }
    }

    @Test
    fun `given some sports fields in db when page number is too high then return empty response`() {
        runBlocking {
            Given()
            dataBuilder.buildSportsField(name = "Field 1")
            dataBuilder.buildSportsField(name = "Field 2")
            val paginationInput = PaginationInput(10, 10)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, null, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields.shouldBeEmpty()
            response.paginationInfo.itemsTotalCount shouldBe 2
        }
    }

    @Test
    fun `given sports fields in db when filter by country then return only matching fields`() {
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
            dataBuilder.buildSportsField(
                name = "Field 2",
                latitude = 48.2089,
                longitude = 16.3726,
                city = "Vienna",
                street = "Hauptstrasse 2",
                zipCode = "1010",
                countryCode = "AUT",
                description = "Test field 2",
            )
            dataBuilder.buildSportsField(
                name = "Field 3",
                latitude = 50.0755,
                longitude = 14.4378,
                city = "Prague",
                street = "Hlavní 3",
                zipCode = "11000",
                countryCode = "CZE",
                description = "Test field 3",
            )
            val paginationInput = PaginationInput(1, 10)
            val filtersInput = SportsFieldFiltersInput(countryCode = "SVK")

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, filtersInput, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields.shouldHaveSize(1).first().run {
                country.code shouldBe "SVK"
            }
        }
    }

    @Test
    fun `given sports fields in db when filter by city then return only matching fields`() {
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
            dataBuilder.buildSportsField(
                name = "Field 2",
                latitude = 48.2089,
                longitude = 16.3726,
                city = "Vienna",
                street = "Hauptstrasse 2",
                zipCode = "1010",
                countryCode = "AUT",
                description = "Test field 2",
            )
            val paginationInput = PaginationInput(1, 10)
            val filtersInput = SportsFieldFiltersInput(city = "Bratislava")

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, filtersInput, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields.shouldHaveSize(1).first().run {
                city shouldBe "Bratislava"
            }
        }
    }

    // TODO after implementing sport types filtering
    @Ignore
    @Test
    fun `given sports fields in db when filter by sport types then return only matching fields`() {
        runBlocking {
            Given()
            val bratislavaSportsFieldDao =
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
            val viennaSportsFieldDao =
                dataBuilder.buildSportsField(
                    name = "Field 2",
                    latitude = 48.2089,
                    longitude = 16.3726,
                    city = "Vienna",
                    street = "Hauptstrasse 2",
                    zipCode = "1010",
                    countryCode = "AUT",
                    description = "Test field 2",
                )
            val soccerDao = repository.findSportTypeByName(SportType.SOCCER.name)
            val basketballDao = repository.findSportTypeByName(SportType.BASKETBALL.name)
            dataBuilder.buildSportsFieldSportsType(
                sportsFieldId = bratislavaSportsFieldDao.getDaoId(),
                sportTypeId = soccerDao.getDaoId(),
            )
            dataBuilder.buildSportsFieldSportsType(
                sportsFieldId = viennaSportsFieldDao.getDaoId(),
                sportTypeId = basketballDao.getDaoId(),
            )
            val paginationInput = PaginationInput(1, 10)
            val filtersInput = SportsFieldFiltersInput(sportTypes = listOf(SportType.BASKETBALL))

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, filtersInput, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields.shouldHaveSize(1).first().run {
                sportTypes shouldHaveSize 1
                sportTypes.first() shouldBe SportType.BASKETBALL
                city shouldBe "Vienna"
            }
        }
    }

    @Test
    fun `given some sports fields in db when get with no filters then return them`() {
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
            dataBuilder.buildSportsField(
                name = "Field 2",
                latitude = 48.2089,
                longitude = 16.3726,
                city = "Vienna",
                street = "Hauptstrasse 2",
                zipCode = "1010",
                countryCode = "AUT",
                description = "Test field 2",
            )
            dataBuilder.buildSportsField(
                name = "Field 3",
                latitude = 50.0755,
                longitude = 14.4378,
                city = "Prague",
                street = "Hlavní 3",
                zipCode = "11000",
                countryCode = "CZE",
                description = "Test field 3",
            )
            val paginationInput = PaginationInput(1, 2)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, null, null),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields shouldHaveSize 2
            response.paginationInfo.itemsTotalCount shouldBe 3
        }
    }

    @Test
    fun `given sports fields in db when sort by name asc then return fields sorted by name in ascending order`() {
        runBlocking {
            Given()
            dataBuilder.buildSportsField(
                name = "Field C",
                latitude = 48.1486,
                longitude = 17.1077,
                city = "Bratislava",
                street = "Hlavná 1",
                zipCode = "81101",
                countryCode = "SVK",
                description = "Test field C",
            )
            dataBuilder.buildSportsField(
                name = "Field A",
                latitude = 48.2089,
                longitude = 16.3726,
                city = "Vienna",
                street = "Hauptstrasse 2",
                zipCode = "1010",
                countryCode = "AUT",
                description = "Test field A",
            )
            dataBuilder.buildSportsField(
                name = "Field B",
                latitude = 50.0755,
                longitude = 14.4378,
                city = "Prague",
                street = "Hlavní 3",
                zipCode = "11000",
                countryCode = "CZE",
                description = "Test field B",
            )
            val paginationInput = PaginationInput(1, 2)
            val sortByInput =
                SportsFieldSortByInput(
                    field = SportsFieldSortByField.NAME,
                    direction = SortByDirection.ASC,
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, null, sortByInput),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields shouldHaveSize 2
            response.sportsFields[0].name shouldBe "Field A"
            response.sportsFields[1].name shouldBe "Field B"
        }
    }

    @Test
    fun `given sports fields in db when sort by name desc filter by city then return fields filtered and sorted by name in descending order`() {
        runBlocking {
            Given()
            dataBuilder.buildSportsField(
                name = "Field C",
                latitude = 48.1486,
                longitude = 17.1077,
                city = "Bratislava",
                street = "Hlavná 1",
                zipCode = "81101",
                countryCode = "SVK",
                description = "Test field C",
            )
            dataBuilder.buildSportsField(
                name = "Field A",
                latitude = 48.1487,
                longitude = 17.1078,
                city = "Bratislava",
                street = "Hlavná 2",
                zipCode = "81101",
                countryCode = "SVK",
                description = "Test field A",
            )
            dataBuilder.buildSportsField(
                name = "Field B",
                latitude = 50.0755,
                longitude = 14.4378,
                city = "Prague",
                street = "Hlavní 3",
                zipCode = "11000",
                countryCode = "CZE",
                description = "Test field B",
            )
            val paginationInput = PaginationInput(1, 10)
            val sortByInput =
                SportsFieldSortByInput(
                    field = SportsFieldSortByField.NAME,
                    direction = SortByDirection.DESC,
                )
            val filters =
                SportsFieldFiltersInput(
                    city = "Bratislava",
                )

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    sportsFieldsQueryRequest(paginationInput, filters, sortByInput),
                    "data.sportsFields",
                    SportsFieldsWrapper::class.java,
                )

            Then()
            response.sportsFields shouldHaveSize 2
            response.sportsFields[0].name shouldBe "Field C"
            response.sportsFields[1].name shouldBe "Field A"
        }
    }

    private val sportsFieldsQueryRequest: (
        paginationInput: PaginationInput,
        filtersInput: SportsFieldFiltersInput?,
        sortByInput: SportsFieldSortByInput?,
    ) -> String = { pagination, filters, sortBy ->
        DgsClient.buildQuery {
            sportsFields(filters = filters, sortBy = sortBy, pagination = pagination) {
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
    }
}