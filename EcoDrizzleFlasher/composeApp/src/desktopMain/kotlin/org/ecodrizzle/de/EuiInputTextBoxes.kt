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
    private val dutyCycleField = EuiInputField()

    @Preview
    @Composable
    fun EuiFields(apiKey: String) {
        var endDeviceId by remember { mutableStateOf("") }
        var joinEui by remember { mutableStateOf("") }
        var devEui by remember { mutableStateOf("") }
        var appKey by remember { mutableStateOf("") }
        var dutyCycle by remember { mutableStateOf(24) }

        var sensorDescription by remember { mutableStateOf("") }
        var isFlashing by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.Start) {
            endDeviceIdField.inputField("DeviceID",14, add0x = false,
                onlyDigits = false, randomGeneration = false, excluteSpecialChar = true, defaultCycle = null) { newValue ->
                endDeviceId = newValue
            }
            joinEuiField.inputField("JoinEUI",16, add0x = true,
                onlyDigits = true, randomGeneration = true, defaultCycle = null) { newValue ->
                joinEui = newValue
            }
            devEuiField.inputField("DevEUI",16, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true, defaultCycle = null) { newValue ->
                devEui = newValue
            }
            appKeyField.inputField("AppKey",32, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true, defaultCycle = null) { newValue ->
                appKey = newValue
            }
            dutyCycleField.inputField("Sende Intervall in Std",3, add0x = false,
                onlyDigits = true, toUpperCase = false, randomGeneration = false, defaultCycle = dutyCycle) { newValue ->
                dutyCycle = try {
                    newValue.toInt()
                }catch (e: Exception){
                    0
                }
            }
            OutlinedTextField(value = sensorDescription,
                onValueChange = {
                    sensorDescription = it
                },
                label = { Text(text = "Sensor Description") },
                modifier = Modifier.width(500.dp).height(100.dp)
            )
            val coroutineScope = rememberCoroutineScope()
            flashComponents(isFlashing, {flashBool -> isFlashing = flashBool}, appKey, endDeviceId, joinEui, devEui, sensorDescription, coroutineScope, apiKey, dutyCycle)
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
                        coroutineScope: CoroutineScope,
                        apiKey: String,
                        dutyCycle: Int,
    ) {
        var flashMessage by remember { mutableStateOf("") }
        var flashStatus by remember { mutableStateOf(false) }
        var ttnStatus by remember { mutableStateOf(false) }

        var buttonState by remember { mutableStateOf(false) }
        if(appKey.isNotBlank() && devEui.isNotBlank() && joinEui.isNotBlank() && endDeviceId.isNotBlank() && dutyCycle != 0) {
            buttonState = true
        }

        Row(modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Button(
                modifier = Modifier.width(150.dp).height(40.dp),
                enabled = buttonState,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isFlashingFunc(true)
                        flashMessage = "📡 Sensor wird im TTN angelegt..."
                        println(apiKey)
                        val apiManager = ApiManager(Credentials(endDeviceId, devEui, joinEui, appKey), sensorDescription, apiKey)
                        val statusCode = apiManager.executeRequestsToTTN()
                        when (statusCode) {
                            HttpStatusCode.OK -> {
                                flashMessage = "✅ Sensor wurde im TTN angelegt"
                                delay(2000)
                                flashMessage = "\uD83D\uDCE5 Flashing beginnt..."
                                val flashComponent = FlashComponent(Credentials(endDeviceId, devEui, joinEui, appKey, dutyCycle = dutyCycle))
                                flashStatus = flashComponent.flashEcoDrizzler()

                                flashMessage = if (flashStatus) "✅ Flashing abgeschlossen!" else "❌ Flashing fehlgeschlagen."
                            }
                            else -> {
                                flashMessage = "❌ Anlegen des Sensors im TTN fehlgeschlagen."
                                ttnStatus = false
                            }
                        }
                        isFlashingFunc(false)
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