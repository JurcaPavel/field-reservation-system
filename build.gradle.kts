import com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.netflix.dgs.codegen") version "7.0.3"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

group = "cz.jurca"
// todo actual version should be provided externally during ci/cd (github actions)
version = "@@VERSION@@"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["netflixDgsVersion"] = "10.1.2"
private val kotestVersion = "5.9.1"
private val kotestExtensionsSpring = "1.3.0"
private val kotestAssertionsArrow = "2.0.0"
private val oshaiKotlinLogging = "7.0.7"
private val arrowKtVersion = "2.1.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowKtVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:$oshaiKotlinLogging")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-client")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework:spring-jdbc")
    implementation("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtensionsSpring")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:2.0.0")
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${property("netflixDgsVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.generateJava {
    packageName = "cz.jurca.fieldreservationsystem.codegen"
    generateClient = true
}

tasks.bootJar {
    archiveFileName.set("field-reservation-system.jar")
}

// Required for Mockito with JDK 21+, see https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#0.3
val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
        useJUnitPlatform()
    }
}

tasks.withType<GenerateJavaTask> {
    language = "kotlin"
    generateClient = true
    generateKotlinNullableClasses = true
    generateKotlinClosureProjections = true
}

ktlint {
    filter {
        // exclude("**/generated/**")
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
    }
}