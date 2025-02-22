package org.ecodrizzle.de

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ApiManager(val credentials: Credentials, var deviceDescription: String) {
    private val applicationID = "sgr-students"
    val getSensorsURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
    val createDeviceURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
    val applicationServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/as/applications/${applicationID}/devices/${credentials.deviceId}"
    val joinServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/js/applications/${applicationID}/devices/${credentials.deviceId}"
    val networkServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/ns/applications/${applicationID}/devices/${credentials.deviceId}"

    private val registerSensorInTTNApiToken =
        ""

    private val apiClient = HttpClient(CIO)
    private val defaultHeaders = headersOf(
        "Accept" to listOf("application/json"),
        "Authorization" to listOf("Bearer $registerSensorInTTNApiToken")
    )

    suspend fun executeRequestsToTTN(): List<String> {
        return try {
            val results = listOf(
                request(createDeviceURL, "POST", credentials = credentials),
                request(applicationServerPutURL, "PUT", credentials = credentials, "applicationServer"),
                request(joinServerPutURL, "PUT", credentials = credentials, "joinServer"),
                request(networkServerPutURL, "PUT", credentials = credentials, "networkServer"),
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

    private fun formatCredentialForTtn(input: String): String {
        return input.replace("0x", "").replace(" ", "").trim()
    }

    suspend fun request(
        requestURL: String,
        requestType: String,
        credentials: Credentials? = null,
        server: String? = null
    ): String {
        var response: HttpResponse? = null
        return try {

            when (requestType) {
                "GET" -> response = apiClient.get(requestURL) {
                    headers.appendAll(defaultHeaders)
                }

                "PUT" -> response = apiClient.put(requestURL) {
                    headers.appendAll(defaultHeaders)
                    credentials?.let {
                        when (server) {
                            "applicationServer" -> setBody(
                                JsonRequestBodies.applicationServerRequestBody(
                                    credentials.deviceId,
                                    formatCredentialForTtn(credentials.devEui),
                                    formatCredentialForTtn(credentials.joinEui),
                                    applicationID
                                )
                            )

                            "joinServer" -> setBody(
                                JsonRequestBodies.joinServerRequestBody(
                                    credentials.deviceId,
                                    formatCredentialForTtn(credentials.appKey),
                                    formatCredentialForTtn(credentials.devEui),
                                    formatCredentialForTtn(credentials.joinEui),
                                    applicationID
                                )
                            )

                            "networkServer" -> setBody(
                                JsonRequestBodies.networkServerRequestBody(
                                    credentials.deviceId,
                                    formatCredentialForTtn(credentials.devEui),
                                    formatCredentialForTtn(credentials.joinEui),
                                    applicationID
                                )
                            )
                        }
                    }
                }

                "POST" -> response = apiClient.post(requestURL) {
                    headers.appendAll(defaultHeaders)
                    credentials?.let {
                        setBody(
                            JsonRequestBodies.createDeviceRequestBody(
                                credentials.deviceId,
                                formatCredentialForTtn(credentials.devEui),
                                formatCredentialForTtn(credentials.joinEui),
                                applicationID,
                                deviceDescription.ifBlank { "Keine spezielle Beschreibung verfasst" }
                            )
                        )
                    }
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
}