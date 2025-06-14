package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.common.runBlocking
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.mock

class LoginUserTest : BaseTest() {
    @Test
    fun `given login user with various types when call isAdmin then it should be correctly evaluated`() {
        Given()
        forAll(
            row(LoginUser(mock(), Username("username"), UserRole.ADMIN, mock()), true),
            row(LoginUser(mock(), Username("username"), UserRole.MANAGER, mock()), false),
            row(LoginUser(mock(), Username("username"), UserRole.BASIC, mock()), false),
        ) { loginUser, expectedResult ->
            When()
            val result = loginUser.isAdmin()

            Then()
            result shouldBe expectedResult
        }
    }

    @Test
    fun `given login user with various types when call isManager then it should be correctly evaluated`() {
        Given()
        forAll(
            row(LoginUser(mock(), Username("username"), UserRole.ADMIN, mock()), false),
            row(LoginUser(mock(), Username("username"), UserRole.MANAGER, mock()), true),
            row(LoginUser(mock(), Username("username"), UserRole.BASIC, mock()), false),
        ) { loginUser, expectedResult ->
            When()
            val result = loginUser.isManager()

            Then()
            result shouldBe expectedResult
        }
    }

    @Test
    fun `given login user is field owner when call isSportsFieldOwner then return true`() = runBlocking {
        Given()
        val loginUser = LoginUser(mock(), Username("username"), UserRole.ADMIN, { _, _ -> true })

        When()
        val result = loginUser.isSportsFieldOwner(mock())

        Then()
        result shouldBe true
    }

    @Test
    fun `given login user is not field owner when call isSportsFieldOwner then return false`() = runBlocking {
        Given()
        val loginUser = LoginUser(mock(), Username("username"), UserRole.ADMIN, { _, _ -> false })

        When()
        val result = loginUser.isSportsFieldOwner(mock())

        Then()
        result shouldBe false
    }


}