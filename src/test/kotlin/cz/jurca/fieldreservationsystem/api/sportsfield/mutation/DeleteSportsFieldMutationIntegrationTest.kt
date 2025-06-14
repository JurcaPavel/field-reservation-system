package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import cz.jurca.fieldreservationsystem.BaseIntegrationTest
import cz.jurca.fieldreservationsystem.codegen.DgsClient
import cz.jurca.fieldreservationsystem.codegen.types.Success
import cz.jurca.fieldreservationsystem.domain.error.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe

class DeleteSportsFieldMutationIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given sports field exists when delete sports field with correct manager then return success`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            setUserInTestSecurityContextHolder(dataBuilder.defaultManager)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    deleteSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString()),
                    "data.deleteSportsField",
                    Success::class.java,
                )

            Then()
            response.run {
                message shouldBe "Sports field deleted successfully by ${dataBuilder.defaultManager.username}"
            }
            repository.findAllSportFields().size shouldBe 0
            repository.findAllSportFieldSportTypes().size shouldBe 0
        }

    @Test
    fun `given sports field exists when delete sports field with admin then return success`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            setUserInTestSecurityContextHolder(dataBuilder.defaultAdmin)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    deleteSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString()),
                    "data.deleteSportsField",
                    Success::class.java,
                )

            Then()
            response.run {
                message shouldBe "Sports field deleted successfully by ${dataBuilder.defaultAdmin.username}"
            }
            repository.findAllSportFields().size shouldBe 0
            repository.findAllSportFieldSportTypes().size shouldBe 0
        }

    @Test
    fun `given sports field exists when delete sports field with different manager then return NotResourceOwnerError`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            val notOwnerManagerDao = dataBuilder.buildUser(username = "pjm2", email = "pjm2@email.com", role = "MANAGER")
            setUserInTestSecurityContextHolder(notOwnerManagerDao)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    deleteSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString()),
                    "data.deleteSportsField",
                    NotResourceOwnerError::class.java,
                )

            Then()
            response.run {
                message shouldBe "User pjm2 cannot delete sports field  with id ${sportsFieldDao.getDaoId()} because he is not the owner of the sports field."
            }
            repository.findAllSportFields().size shouldBe 1
            repository.findAllSportFieldSportTypes().size shouldBe 2
        }

    @Test
    fun `given sports field exists when delete sports field with basic user then return NotManagerOrAdminError`() =
        runBlocking {
            Given()
            val sportsFieldDao = dataBuilder.buildSportsField()
            val basicUser = dataBuilder.buildUser(username = "pj", email = "pj@email.com", role = "BASIC")
            setUserInTestSecurityContextHolder(basicUser)

            When()
            val response =
                dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    deleteSportsFieldMutationRequest(sportsFieldDao.getDaoId().toString()),
                    "data.deleteSportsField",
                    NotManagerOrAdminError::class.java,
                )

            Then()
            response.run {
                message shouldBe "User pj cannot delete sports field  with id ${sportsFieldDao.getDaoId()} because he is not a manager or admin."
            }
            repository.findAllSportFields().size shouldBe 1
            repository.findAllSportFieldSportTypes().size shouldBe 2
        }

    private val deleteSportsFieldMutationRequest: (id: String) -> String = { fieldId ->
        DgsClient.buildMutation {
            deleteSportsField(fieldId) {
                onSuccess {
                    message
                }
                onNotFoundError { message }
                onNotResourceOwnerError { message }
                onNotManagerOrAdminError { message }
            }
        }
    }
}