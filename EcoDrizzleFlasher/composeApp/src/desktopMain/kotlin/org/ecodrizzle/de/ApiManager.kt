package org.ecodrizzle.de

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun getAllSensors(): String {
    val client = HttpClient(CIO)
    val getAllSensorsApiToken = ""

    return try {
        val response: HttpResponse =
            client.get("https://zde.eu1.cloud.thethings.industries/api/v3/applications/sgr-students/devices") {
                headers.append(HttpHeaders.Authorization, "Bearer $getAllSensorsApiToken")
            }
        println(response.status)
        println(response.bodyAsText())
        response.bodyAsText()
    } catch (e: Exception) {
        "Error: ${e.message}"
    } finally {
        client.close()
    }
}