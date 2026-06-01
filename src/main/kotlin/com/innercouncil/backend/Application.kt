package com.innercouncil.backend

import com.innercouncil.backend.config.AppSettings
import com.innercouncil.backend.db.DatabaseFactory
import com.innercouncil.backend.di.appModule
import com.innercouncil.backend.routes.configureRouting
import com.innercouncil.backend.serialization.InstantSerializer
import com.innercouncil.backend.serialization.LocalDateSerializer
import com.innercouncil.backend.serialization.UUIDSerializer
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.plugins.swagger.swaggerUI
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val settings = AppSettings.from(environment.config)

    install(CallLogging)
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                serializersModule = SerializersModule {
                    contextual(UUID::class, UUIDSerializer)
                    contextual(LocalDate::class, LocalDateSerializer)
                    contextual(Instant::class, InstantSerializer)
                }
            },
        )
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException -> call.respond(HttpStatusCode.BadRequest, ApiErrorResponse(message = cause.message ?: "Invalid request"))
                is ApiException -> call.respond(cause.status, ApiErrorResponse(message = cause.message))
                else -> {
                    call.application.environment.log.error("Unhandled request error", cause)
                    call.respond(HttpStatusCode.InternalServerError, ApiErrorResponse(message = "Internal server error"))
                }
            }
        }
    }
    install(Koin) {
        modules(appModule(settings))
    }

    val databaseFactory: DatabaseFactory = get()
    databaseFactory.init()

    runBlocking {
        get<com.innercouncil.backend.services.CharacterSeeder>().seedCharacters()
    }

    routing {
        get("/openapi") {
            val specification = requireNotNull(javaClass.classLoader.getResource("openapi/documentation.yaml")) {
                "OpenAPI specification file is missing"
            }.readText()
            call.respondText(specification, ContentType.parse("application/yaml"))
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    configureRouting()
}

@Serializable
data class ApiErrorResponse(
    val message: String,
)

class ApiException(
    val status: HttpStatusCode,
    override val message: String,
) : RuntimeException(message)
