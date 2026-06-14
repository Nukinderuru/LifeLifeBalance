package com.innercouncil.backend.routes

import com.innercouncil.backend.dto.CreateCompletionRequest
import com.innercouncil.backend.dto.CreateWishRequest
import com.innercouncil.backend.dto.UpdateWishRequest
import com.innercouncil.backend.models.WishCategory
import com.innercouncil.backend.services.CharacterService
import com.innercouncil.backend.services.CompletionService
import com.innercouncil.backend.services.DashboardService
import com.innercouncil.backend.services.WishService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.util.UUID

fun Application.configureRouting() {
    val characterService by inject<CharacterService>()
    val wishService by inject<WishService>()
    val completionService by inject<CompletionService>()
    val dashboardService by inject<DashboardService>()

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/api") {
            route("/characters") {
                get {
                    call.respond(characterService.list())
                }

                get("/{id}") {
                    call.respond(characterService.get(call.uuidPathParameter("id")))
                }

                get("/{id}/summary") {
                    val date = call.localDateQueryParameter("date")
                    call.respond(characterService.getSummary(call.uuidPathParameter("id"), date))
                }
            }

            route("/wishes") {
                get {
                    val characterId = call.request.queryParameters["characterId"]?.let { UUID.fromString(it) }
                    val category = call.request.queryParameters["category"]?.let { WishCategory.valueOf(it) }
                    val active = call.request.queryParameters["active"]?.let {
                        when (it) {
                            "true" -> true
                            "false" -> false
                            else -> throw IllegalArgumentException("Invalid active filter")
                        }
                    }
                    call.respond(wishService.list(characterId, category, active))
                }

                get("/{id}") {
                    call.respond(wishService.get(call.uuidPathParameter("id")))
                }

                post {
                    val response = wishService.create(call.receive<CreateWishRequest>())
                    call.respond(HttpStatusCode.Created, response)
                }

                put("/{id}") {
                    call.respond(wishService.update(call.uuidPathParameter("id"), call.receive<UpdateWishRequest>()))
                }

                delete("/{id}") {
                    wishService.softDelete(call.uuidPathParameter("id"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/completions") {
                get {
                    call.respond(completionService.listByDate(call.localDateQueryParameter("date")))
                }

                post {
                    val response = completionService.create(call.receive<CreateCompletionRequest>())
                    call.respond(HttpStatusCode.Created, response)
                }

                delete("/{id}") {
                    completionService.delete(call.uuidPathParameter("id"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/dashboard") {
                get("/today") {
                    call.respond(dashboardService.getToday())
                }

                get("/week") {
                    val endDate = call.request.queryParameters["endDate"]?.let { LocalDate.parse(it) } ?: LocalDate.now()
                    call.respond(dashboardService.getWeek(endDate))
                }
            }
        }
    }
}
