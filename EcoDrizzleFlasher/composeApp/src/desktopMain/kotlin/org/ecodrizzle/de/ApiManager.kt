package org.ecodrizzle.de

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val appName = "sgr-students"
val appKey = ""
val devEUI = ""
val joinEUI = ""
val deviceID = "Linus-Test-Sensor"

const val getAllSensorsApiToken = ""
const val registerSensorInTTNApiToken = ""

val getSensorsURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${appName}/devices"
val applicationServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/as/applications/${appName}/devices/${deviceID}"
val joinServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/js/applications/${appName}/devices/${deviceID}"
val networkServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/ns/applications/${appName}/devices/${deviceID}"

val apiClient = HttpClient(CIO)
val defaultHeaders = headersOf(
    "Accept" to listOf("application/json"),
    "Authorization" to listOf("Bearer $getAllSensorsApiToken")
)

suspend fun executeRequestsToTTN(): List<String> {
    return try {
        val results = listOf(
            request(applicationServerPutURL, "PUT"),
            request(joinServerPutURL, "PUT"),
            request(networkServerPutURL, "PUT"),
            request(getSensorsURL, "GET")
        )
        println(results)
        results
    } catch (e: Exception) {
        listOf("Error : ${e.message}")
    } finally {
        apiClient.close()
    }
}

// Register Sensor
suspend fun request(requestURL: String, requestType: String): String {
    var response: HttpResponse? = null

    return try {
        when (requestType) {
            "GET" -> response = apiClient.get(requestURL) {
                headers.appendAll(defaultHeaders)
            }
            "PUT" -> response = apiClient.put(requestURL) {
                headers.appendAll(defaultHeaders)
            }
        }
        response?.let {
            println(it.status)
            it.bodyAsText()
        } ?: "No Response received"
    } catch (e: Exception) {
        "Error ${e.message}"
    }
}