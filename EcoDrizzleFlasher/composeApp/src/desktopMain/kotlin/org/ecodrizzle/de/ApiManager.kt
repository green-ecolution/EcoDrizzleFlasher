package org.ecodrizzle.de

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

const val applicationID = "sgr-students"
val appKey = ""
val devEUI = ""
val joinEUI = ""
val deviceID = "Linus-Test-Sensor"

const val getAllSensorsApiToken = ""
const val registerSensorInTTNApiToken = ""

const val getSensorsURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
const val createDeviceURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
val applicationServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/as/applications/${applicationID}/devices/${deviceID}"
val joinServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/js/applications/${applicationID}/devices/${deviceID}"
val networkServerPutURL = "https://zde.eu1.cloud.thethings.industries/api/v3/ns/applications/${applicationID}/devices/${deviceID}"

val apiClient = HttpClient(CIO)
val defaultHeaders = headersOf(
    "Accept" to listOf("application/json"),
    "Authorization" to listOf("Bearer $getAllSensorsApiToken")
)

suspend fun executeRequestsToTTN(): List<String> {
    return try {
        val results = listOf(
            request(createDeviceURL, "POST"),
            request(applicationServerPutURL, "PUT", "applicationServer"),
            request(joinServerPutURL, "PUT", "joinServer"),
            request(networkServerPutURL, "PUT", "networkServer"),
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

suspend fun request(requestURL: String, requestType: String, server: String? = null): String {
    var response: HttpResponse? = null

    return try {
        when (requestType) {
            "GET" -> response = apiClient.get(requestURL) {
                headers.appendAll(defaultHeaders)
            }
            "PUT" -> response = apiClient.put(requestURL) {
                headers.appendAll(defaultHeaders)
                when (server) {
                    "applicationServer" -> setBody(JsonRequestBodies.applicationServerRequestBody(deviceID, devEUI, joinEUI, applicationID))
                    "joinServer" -> setBody(JsonRequestBodies.joinServerRequestBody(deviceID, appKey, devEUI, joinEUI, applicationID))
                    "networkServer" -> setBody(JsonRequestBodies.networkServerRequestBody(deviceID, devEUI, joinEUI, applicationID))
                }
            }
            "POST" -> response = apiClient.post(requestURL) {
                headers.appendAll(defaultHeaders)
                setBody(JsonRequestBodies.createDeviceRequestBody(deviceID, devEUI, joinEUI, applicationID))
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