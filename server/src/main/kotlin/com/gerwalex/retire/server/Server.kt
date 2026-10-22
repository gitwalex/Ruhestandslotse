package com.gerwalex.retire.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(val status: String, val message: String)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respondText("Ruhestandslotse Server is running!")
            }
            get("/api/status") {
                call.respond(StatusResponse("success", "Ruhestandslotse backend is up and running"))
            }
        }
    }.start(wait = true)
}
