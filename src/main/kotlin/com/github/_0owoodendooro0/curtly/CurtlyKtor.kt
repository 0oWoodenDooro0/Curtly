package com.github._0owoodendooro0.curtly

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.curtlyRouting(service: CurtlyService) {
    get("/{key}") {
        val key = call.parameters["key"]
        if (key != null) {
            val longUrl = service.resolve(key)
            if (longUrl != null) {
                call.respondRedirect(longUrl)
            } else {
                call.respond(HttpStatusCode.NotFound, "Short URL not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing key")
        }
    }
}
