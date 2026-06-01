package com.innercouncil.backend.routes

import io.ktor.server.application.ApplicationCall
import java.time.LocalDate
import java.util.UUID

fun ApplicationCall.uuidPathParameter(name: String): UUID =
    parameters[name]?.let(UUID::fromString) ?: throw IllegalArgumentException("Missing path parameter: $name")

fun ApplicationCall.localDateQueryParameter(name: String): LocalDate =
    request.queryParameters[name]?.let(LocalDate::parse) ?: throw IllegalArgumentException("Missing query parameter: $name")
