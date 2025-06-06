package cz.jurca.fieldreservationsystem

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.test.EnableDgsTest
import cz.jurca.fieldreservationsystem.repository.SportTypeRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldRepository
import cz.jurca.fieldreservationsystem.repository.SportsFieldSportTypeRepository
import io.kotest.extensions.spring.SpringExtension
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@ActiveProfiles("test")
@SpringBootTest(classes = [FieldReservationSystemApplication::class])
@EnableDgsTest
abstract class BaseIntegrationTest : BaseTest() {
    // reuse the PostgreSQL container across tests to speed up the test execution
    companion object {
        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:16.2"))
                .withDatabaseName("field_reservation")
                .withUsername("field_reservation_db_user")
                .withPassword("field_reservation_password@666")
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
                .withCreateContainerCmdModifier { cmd -> cmd.withName("field_reservation_db_test") }
                .withReuse(true)
                .apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/${postgresContainer.databaseName}" }
            registry.add("spring.r2dbc.username", postgresContainer::getUsername)
            registry.add("spring.r2dbc.password", postgresContainer::getPassword)
            registry.add("spring.flyway.url", postgresContainer::getJdbcUrl)
            registry.add("spring.flyway.user", postgresContainer::getUsername)
            registry.add("spring.flyway.password", postgresContainer::getPassword)
        }
    }

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    protected lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Autowired
    protected lateinit var sportsFieldRepository: SportsFieldRepository

    @Autowired
    protected lateinit var sportTypeRepository: SportTypeRepository

    @Autowired
    protected lateinit var sportsFieldSportTypeRepository: SportsFieldSportTypeRepository

    @BeforeEach
    fun cleanupDatabase() {
        runBlocking {
            sportsFieldSportTypeRepository.deleteAll()
            sportTypeRepository.deleteAll()
            sportsFieldRepository.deleteAll()
        }
    }
}