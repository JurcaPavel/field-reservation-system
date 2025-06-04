package cz.jurca.field_reservation_system

import cz.jurca.field_reservation_system.repository.SportsFieldDao
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SportsFieldIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `given field in db when get then return correct one`() {
        runBlocking {
            Given()
            sportsFieldRepository.save(SportsFieldDao("Field 1"))

            When()
            val sportsField = sportsFieldRepository.findAll().first()

            Then()
            sportsField.run {
                name shouldBe "Field 1"
            }
        }
    }
}