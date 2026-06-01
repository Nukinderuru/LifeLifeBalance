package com.innercouncil.backend

import com.innercouncil.backend.dto.CharacterResponse
import com.innercouncil.backend.dto.CompletionResponse
import com.innercouncil.backend.dto.CreateCompletionRequest
import com.innercouncil.backend.dto.CreateWishRequest
import com.innercouncil.backend.dto.TodayDashboardResponse
import com.innercouncil.backend.dto.WeeklyDashboardResponse
import com.innercouncil.backend.dto.WishResponse
import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.WishCategory
import com.innercouncil.backend.serialization.InstantSerializer
import com.innercouncil.backend.serialization.LocalDateSerializer
import com.innercouncil.backend.serialization.UUIDSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import java.sql.SQLException

object TestPostgres {
    private var container: PostgreSQLContainer<Nothing>? = null

    fun getOrStart(): PostgreSQLContainer<Nothing> {
        val dockerAvailable = try {
            DockerClientFactory.instance().isDockerAvailable
        } catch (_: IllegalStateException) {
            false
        }
        assumeTrue(dockerAvailable, "Docker is required for PostgreSQL integration tests")
        return container ?: PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("inner_council_test")
            withUsername("inner_council")
            withPassword("inner_council")
            start()
        }.also { container = it }
    }
}

val testJson = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(UUID::class, UUIDSerializer)
        contextual(LocalDate::class, LocalDateSerializer)
        contextual(Instant::class, InstantSerializer)
    }
}

fun testApp(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
    val postgres = TestPostgres.getOrStart()
    cleanDatabase(postgres)
    testApplication {
        environment {
            config = MapApplicationConfig(
                "app.database.jdbcUrl" to postgres.jdbcUrl,
                "app.database.username" to postgres.username,
                "app.database.password" to postgres.password,
                "app.database.driverClassName" to "org.postgresql.Driver",
                "app.database.maximumPoolSize" to "3",
            )
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(testJson)
            }
            defaultRequest {
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
        }
        block(client)
    }
}

fun cleanDatabase(postgres: PostgreSQLContainer<Nothing>) {
    try {
        DriverManager.getConnection(
            postgres.jdbcUrl,
            postgres.username,
            postgres.password,
        ).use { connection ->
            connection.createStatement().use { statement ->
                statement.execute("TRUNCATE TABLE completions, wishes, characters CASCADE")
            }
        }
    } catch (_: SQLException) {
        // The first test run truncates before migrations create the tables.
    }
}

suspend fun HttpClient.fetchCharacters(): List<CharacterResponse> = get("/api/characters").body()

suspend fun HttpClient.createWish(characterId: UUID, title: String, points: Int, category: WishCategory = WishCategory.DAILY): WishResponse =
    post("/api/wishes") {
        contentType(ContentType.Application.Json)
        setBody(
            CreateWishRequest(
                characterId = characterId,
                title = title,
                description = null,
                points = points,
                category = category,
                active = true,
            ),
        )
    }.body()

suspend fun HttpClient.createCompletion(wishId: UUID, date: LocalDate, notes: String? = null): CompletionResponse =
    post("/api/completions") {
        contentType(ContentType.Application.Json)
        setBody(CreateCompletionRequest(wishId = wishId, date = date, notes = notes))
    }.body()

suspend fun HttpClient.getTodayDashboard(): TodayDashboardResponse = get("/api/dashboard/today").body()

suspend fun HttpClient.getWeekDashboard(endDate: LocalDate): WeeklyDashboardResponse =
    get("/api/dashboard/week?endDate=$endDate").body()

suspend fun HttpClient.findCharacterByCode(code: CharacterCode): CharacterResponse = fetchCharacters().first { it.code == code }
