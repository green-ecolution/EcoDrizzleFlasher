package org.ecodrizzle.de

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EuiInputTextBoxes {
    private val endDeviceIdField = EuiInputField()
    private val joinEuiField = EuiInputField()
    private val devEuiField = EuiInputField()
    private val appKeyField = EuiInputField()

    @Preview
    @Composable
    fun EuiFields() {
        var endDeviceId by remember { mutableStateOf("") }
        var joinEui by remember { mutableStateOf("") }
        var devEui by remember { mutableStateOf("") }
        var appKey by remember { mutableStateOf("") }

        var sensorDescription by remember { mutableStateOf("") }
        var isFlashing by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.Start) {
            endDeviceIdField.inputField("DeviceID",14, add0x = false,
                onlyDigits = false, toLowerCase = true, randomGeneration = false) { newValue ->
                endDeviceId = newValue
            }
            joinEuiField.inputField("JoinEUI",16, add0x = true,
                onlyDigits = true, randomGeneration = true) { newValue ->
                joinEui = newValue
            }
            devEuiField.inputField("DevEUI",16, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true) { newValue ->
                devEui = newValue
            }
            appKeyField.inputField("AppKey",32, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true) { newValue ->
                appKey = newValue
            }
            OutlinedTextField(value = sensorDescription,
                onValueChange = {
                    sensorDescription = it
                },
                label = { Text(text = "Sensor Description") },
                modifier = Modifier.width(500.dp).height(100.dp)
            )
            val coroutineScope = rememberCoroutineScope()
            flashComponents(isFlashing, {flashBool -> isFlashing = flashBool}, appKey, endDeviceId, joinEui, devEui, sensorDescription, coroutineScope)
        }
    }

    @Composable
    fun flashComponents(isFlashing: Boolean,
                        isFlashingFunc: (Boolean) -> Unit,
                        appKey: String,
                        endDeviceId: String,
                        joinEui: String,
                        devEui: String,
                        sensorDescription:String,
                        coroutineScope: CoroutineScope) {
        var flashMessage by remember { mutableStateOf("Bitte Alle Felder Ausfüllen") }
        if (isFlashing) {
            CircularProgressIndicator()  // Show loading spinner
            Spacer(modifier = Modifier.height(16.dp))
            Text(flashMessage)
        } else {
            if(appKey.isNotBlank() && devEui.isNotBlank() && joinEui.isNotBlank() && endDeviceId.isNotBlank()) {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isFlashingFunc(true)
                        flashMessage = "Sensor wird im TTN angelegt"
                        val apiManager = ApiManager(Credentials(endDeviceId, devEui, joinEui, appKey), sensorDescription)

                        val statusCode = apiManager.executeRequestsToTTN()
                        when (statusCode) {
                            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden, HttpStatusCode.InternalServerError -> {
                                flashMessage = "Anlegen des Sensors im TTN fehlgeschlagen"
                                isFlashingFunc(false)
                            }
                            HttpStatusCode.OK -> {
                                flashMessage = "Sensor wurde im TTN angelegt"
                                delay(2000)
                                flashMessage = "Sensor wird geflasht"
                                try {
                                    val flashComponent = FlashComponent(Credentials(endDeviceId, devEui, joinEui, appKey))
                                    flashComponent.flashEcoDrizzler()
                                    flashMessage = "✅ Flashing complete!"
                                    delay(2000)
                                } catch (e: Exception) {
                                    flashMessage = "❌ Error: ${e.localizedMessage}"
                                    delay(2000)
                                } finally {
                                    isFlashingFunc(false)
                                }
                            }
                            else -> flashMessage = "Unknown error"
                        }
                    }
                }) {
                    Text("Sensor Flashen")
                }
            } else {
                Text(flashMessage)
            }
        }
    }
}