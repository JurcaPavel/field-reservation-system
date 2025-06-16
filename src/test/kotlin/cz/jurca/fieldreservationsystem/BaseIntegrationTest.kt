package cz.jurca.fieldreservationsystem

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.test.EnableDgsTest
import cz.jurca.fieldreservationsystem.configuration.SecurityConfiguration
import cz.jurca.fieldreservationsystem.db.repository.UserDao
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.*

@ActiveProfiles("test")
@SpringBootTest(classes = [FieldReservationSystemApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var dataBuilder: TestDataBuilder

    @Autowired
    protected lateinit var repository: TestRepository

    @BeforeEach
    fun prepareTestCase() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        dataBuilder.deleteAll()
        dataBuilder.buildInitialData()
    }

    protected fun setUserInTestSecurityContextHolder(user: UserDao) {
        val userDetails =
            SecurityConfiguration.CustomUserDetails(
                id = user.getDaoId().value,
                username = user.username,
                password = user.password,
                role = user.role,
            )
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        TestSecurityContextHolder.getContext().authentication = authentication
    }
}