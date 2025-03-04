package org.ecodrizzle.de

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ApiManager(private val credentials: Credentials, private var deviceDescription: String) {
    private val applicationID = "sgr-students"
    private val getSensorsURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
    private val createDeviceURL = "https://zde.eu1.cloud.thethings.industries/api/v3/applications/${applicationID}/devices"
    private val applicationServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/as/applications/${applicationID}/devices/${credentials.deviceId}"
    private val joinServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/js/applications/${applicationID}/devices/${credentials.deviceId}"
    private val networkServerPutURL =
        "https://zde.eu1.cloud.thethings.industries/api/v3/ns/applications/${applicationID}/devices/${credentials.deviceId}"

    data class Endpoint(
        val url: String,
        val requestType: String,
        val credentials: Credentials? = null,
        val server: String? = "No Server specified",
        val additionalInfo: String? = "No additional info"
    )

    private val endpoints = listOf(
        Endpoint(getSensorsURL, requestType = "GET", credentials, additionalInfo = "Get all devices"),
        Endpoint(createDeviceURL, requestType = "POST", credentials, additionalInfo = "Create a new device"),
        Endpoint(applicationServerPutURL, requestType = "PUT", credentials, server = "applicationServer", additionalInfo = "Create Device on Application-Server"),
        Endpoint(joinServerPutURL, requestType = "PUT", credentials, server = "joinServer", additionalInfo = "Create Device on Application-Server"),
        Endpoint(networkServerPutURL, requestType = "PUT", credentials, server = "networkServer",additionalInfo = "Create Device on Application-Server")
    )

    private val registerSensorInTTNApiToken =
        ""

    private val apiClient = HttpClient(CIO)
    private val defaultHeaders = headersOf(
        "Accept" to listOf("application/json"),
        "Authorization" to listOf("Bearer $registerSensorInTTNApiToken")
    )

    suspend fun executeRequestsToTTN(): HttpStatusCode {
        return try {
            var result: HttpStatusCode = HttpStatusCode.OK

            for(endpoint in endpoints) {
                val response = request(endpoint.url, requestType = endpoint.requestType, credentials = credentials, server = endpoint.server, additionalInfo = endpoint.additionalInfo)
                if(response.value in 400..599){
                    result = response
                    println(response.value)
                }
            }
            println(result)
            result
        } catch (e: Exception) {
            HttpStatusCode.InternalServerError
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
        server: String? = "No specific Server",
        additionalInfo: String? = "no further Infos",
    ): HttpStatusCode {
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
            response?.status ?: HttpStatusCode.ServiceUnavailable
        } catch (e: Exception) {
            HttpStatusCode.InternalServerError
        }
    }
}