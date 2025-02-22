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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            endDeviceIdField.inputField("DeviceID",16, add0x = false,
                onlyDigits = false, toLowerCase = true, randomGeneration = false) { newValue ->
                endDeviceId = newValue
            }
            joinEuiField.inputField("JoinEUI",16, add0x = true,
                onlyDigits = true, randomGeneration = false) { newValue ->
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

            var responseText by remember { mutableStateOf("Noch keine Daten geladen...") }
            //Text(responseText, modifier = Modifier.padding(10.dp), style = TextStyle(fontSize = 12.sp))

            val coroutineScope = rememberCoroutineScope()
            val apiManager = ApiManager(Credentials(endDeviceId, devEui, joinEui, appKey), sensorDescription)
            getSensorsButton(
                onClick = {
                    coroutineScope.launch {
                        responseText = apiManager.executeRequestsToTTN().joinToString(separator = "\n")
                    }
                }
            )

            var resultText by remember { mutableStateOf("Hier sollten die formatierten Inputs auftauchen!") }
            Button(onClick = {resultText = "EndDeviceID : $endDeviceId \nJoinEUI: $joinEui \nDevEUI: $devEui \nAppKEY: $appKey" }) {
                Text(text = "Eingaben speichern")
            }
            Text(resultText, style = TextStyle(fontSize = 12.sp))
            flashComponents(isFlashing, {flashBool -> isFlashing = flashBool}, appKey, endDeviceId, joinEui, devEui, coroutineScope)
        }
    }

    @Composable
    fun getSensorsButton(onClick: () -> Unit){
        Button(onClick = onClick) {
            Text("Sensoren abfragen")
        }
    }

    @Composable
    fun flashComponents(isFlashing: Boolean, isFlashingFunc: (Boolean) -> Unit, appKey: String, endDeviceId: String, joinEui: String, devEui: String, coroutineScope: CoroutineScope) {
        var flashMessage by remember { mutableStateOf("") }
        if (isFlashing) {
            CircularProgressIndicator()  // Show loading spinner
            Spacer(modifier = Modifier.height(16.dp))
            Text(flashMessage)
        } else {
            if(appKey.isNotBlank() && devEui.isNotBlank() && joinEui.isNotBlank() && endDeviceId.isNotBlank()) {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isFlashingFunc(true)
                        flashMessage = "Flashing EcoDrizzler..."
                        try {
                            val flashComponent = FlashComponent(Credentials(endDeviceId, devEui, joinEui, appKey))
                            flashComponent.flashEcoDrizzler()
                            flashMessage = "✅ Flashing complete!"
                        } catch (e: Exception) {
                            flashMessage = "❌ Error: ${e.localizedMessage}"
                        } finally {
                            isFlashingFunc(false)
                        }
                    }
                }) {
                    Text("Flash EcoDrizzler")
                }
            }
        }
    }
}