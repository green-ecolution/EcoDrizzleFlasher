package org.ecodrizzle.de

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        var flashMessage by remember { mutableStateOf("") }
        var flashStatus by remember { mutableStateOf(false) }
        var ttnStatus by remember { mutableStateOf(false) }

        var buttonState by remember { mutableStateOf(false) }
        if(appKey.isNotBlank() && devEui.isNotBlank() && joinEui.isNotBlank() && endDeviceId.isNotBlank()) {
            buttonState = true
        }

        Row(modifier = Modifier.padding(top = 10.dp) ,horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Button(
                modifier = Modifier.width(150.dp).height(40.dp),
                enabled = buttonState,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isFlashingFunc(true)

                        val flashComponent =
                            FlashComponent(Credentials(endDeviceId, devEui, joinEui, appKey))
                        flashStatus = flashComponent.flashEcoDrizzler()

                        when (flashStatus) {
                            false -> {
                                flashMessage = "❌ Microcontroller konnte nicht geflasht werden"
                                isFlashingFunc(false)
                            }
                            true -> {
                                val apiManager = ApiManager(Credentials(endDeviceId, devEui, joinEui, appKey), sensorDescription)
                                val statusCode = apiManager.executeRequestsToTTN()

                                flashMessage = "✅ Flashing complete!"
                                delay(2000)

                                when (statusCode) {
                                    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden, HttpStatusCode.InternalServerError, HttpStatusCode.BadRequest -> {
                                        flashMessage = "❌ Anlegen des Sensors im TTN fehlgeschlagen"
                                        ttnStatus = false
                                        isFlashingFunc(false)
                                    }

                                    HttpStatusCode.OK -> {
                                        flashMessage = "✅ Sensor wurde im TTN angelegt"
                                        ttnStatus = true
                                        isFlashingFunc(false)
                                    }
                                    else -> flashMessage = "Unknown error"
                                }
                            }
                        }
                    }
                }) {

                if(isFlashing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp)
                } else {
                    Text("Sensor Flashen")
                }
            }
            Spacer(Modifier.width(20.dp))
            when (ttnStatus && flashStatus) {
                true -> Text(flashMessage)
                false -> Text(flashMessage, fontWeight = FontWeight.Bold)
            }
        }
    }
}