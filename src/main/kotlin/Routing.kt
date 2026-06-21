package com.github._0owoodendooro0

import com.github._0owoodendooro0.curtly.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class ShortenRequest(
    val url: String,
    val key: String? = null
)

@Serializable
data class ShortenResponse(
    val shortUrl: String,
    val key: String,
    val longUrl: String
)

val fileStorage = FileUrlStorage(File("data/urls.db"))
val curtlyService = CurtlyService(
    storage = fileStorage,
    baseUrl = "http://localhost:8080"
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Curtly URL Shortener is running! Try POSTing to /shorten with a JSON body: {\"url\": \"https://example.com\"}")
        }

        post("/shorten") {
            try {
                val request = runCatching { call.receive<ShortenRequest>() }.getOrNull()
                val url = request?.url ?: call.parameters["url"]
                val customKey = request?.key ?: call.parameters["key"]

                if (url.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or empty 'url' parameter"))
                    return@post
                }

                val shortUrl = curtlyService.shorten(url, customKey)
                val generatedKey = shortUrl.substringAfterLast("/")

                call.respond(
                    HttpStatusCode.Created,
                    ShortenResponse(
                        shortUrl = shortUrl,
                        key = generatedKey,
                        longUrl = url
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "Invalid key or URL conflict")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Internal server error")))
            }
        }

        curtlyRouting(curtlyService)
    }
}