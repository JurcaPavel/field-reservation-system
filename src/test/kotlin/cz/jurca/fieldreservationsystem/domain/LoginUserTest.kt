package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.BaseTest
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class LoginUserTest : BaseTest() {
    @Test
    fun `given login user with various types when call isAdmin then it should be correctly evaluated`() {
        Given()
        forAll(
            row(LoginUser(1, Username("username"), UserRole.ADMIN), true),
            row(LoginUser(1, Username("username"), UserRole.MANAGER), false),
            row(LoginUser(1, Username("username"), UserRole.BASIC), false),
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
            row(LoginUser(1, Username("username"), UserRole.ADMIN), false),
            row(LoginUser(1, Username("username"), UserRole.MANAGER), true),
            row(LoginUser(1, Username("username"), UserRole.BASIC), false),
        ) { loginUser, expectedResult ->
            When()
            val result = loginUser.isManager()

            Then()
            result shouldBe expectedResult
        }
    }
}