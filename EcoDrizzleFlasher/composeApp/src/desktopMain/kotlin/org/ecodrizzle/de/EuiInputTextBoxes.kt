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

        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.Start) {
            endDeviceIdField.inputField("DeviceID",16, add0x = false,
                onlyDigits = false, toUpperCase = false, randomGeneration = false) { newValue ->
                endDeviceId = newValue // Aktualisiert den Wert in der übergeordneten Composable
            }
            joinEuiField.inputField("JoinEUI",16, add0x = true,
                onlyDigits = true, toUpperCase = false, randomGeneration = false) { newValue ->
                joinEui = newValue // Aktualisiert den Wert in der übergeordneten Composable
            }
            devEuiField.inputField("DevEUI",16, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true) { newValue ->
                devEui = newValue // Aktualisiert den Wert in der übergeordneten Composable
            }
            appKeyField.inputField("AppKey",32, add0x = true,
                onlyDigits = false, toUpperCase = true, randomGeneration = true) { newValue ->
                appKey = newValue // Aktualisiert den Wert in der übergeordneten Composable
            }

            var responseText by remember { mutableStateOf("Noch keine Daten geladen...") }
            Text(responseText, modifier = Modifier.padding(10.dp), style = TextStyle(fontSize = 12.sp))

            val coroutineScope = rememberCoroutineScope()
            getSensorsButton(
                onClick = {
                    coroutineScope.launch {
                        responseText = getAllSensors()
                    }
                }
            )

            var resultText by remember { mutableStateOf("Hier sollten die formatierten Inputs auftauchen!") }
            Button(onClick = {resultText = "EndDeviceID : $endDeviceId \nJoinEUI: $joinEui \nDevEUI: $devEui \nAppKEY: $appKey" }) {
                Text(text = "Eingaben speichern")
            }
            Text(resultText, style = TextStyle(fontSize = 12.sp))
        }
    }

    @Composable
    fun getSensorsButton(onClick: () -> Unit){
        Button(onClick = onClick) {
            Text("Sensoren abfragen")
        }
    }
}