package org.ecodrizzle.de

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class EuiInputTextBoxes {
    private var endDeviceIdLength = 16
    private var joinEuiLength = 16
    private var devEuiLength = 16
    private var appKeyLength = 32

    @Preview
    @Composable
    fun EuiFields() {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.Start) {
            var endDeviceId by remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    singleLine = true,
                    value = endDeviceId,
                    onValueChange = {
                        if(it.length <= endDeviceIdLength) {
                            endDeviceId = it.replace(" ", "-")
                        }
                    },
                    label = { Text("End-Device-ID") })
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    Text(text = "Device ID: $endDeviceId", style = TextStyle(fontSize = 12.sp))
                }
            }

            var joinEUI by remember { mutableStateOf("") }
            var formatedJoinEUI = ""
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    singleLine = true,
                    value = joinEUI,
                    onValueChange = {
                        if(it.length <= joinEuiLength && it.all{char -> char.isDigit()}) {
                            joinEUI = it.replace(" ", "")
                        }
                    },
                    label = { Text("JoinEUI") })
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    formatedJoinEUI = formatInputToMSB(joinEUI)
                    Text(text = "JoinEUI: $formatedJoinEUI", style = TextStyle(fontSize = 12.sp))
                }
            }

            var devEui by remember { mutableStateOf("") } // Nur der Rohtext ohne Leerzeichen
            var formatedDevEUI = ""
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    singleLine = true,
                    value = devEui,
                    onValueChange = {
                        if (it.length <= devEuiLength) {
                            devEui = it.replace(" ", "").toUpperCase(Locale.current)
                        }
                    },
                    label = { Text("DevEUI") }
                )
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)) {
                    formatedDevEUI = formatInputToMSB(devEui)
                    Text(text = "DevEUI: $formatedDevEUI", style = TextStyle(fontSize = 12.sp))
                }
            }

            var appKey by remember { mutableStateOf("") }
            var formattedAppKey = ""
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    singleLine = true,
                    value = appKey,
                    onValueChange = {
                        if(it.length <= appKeyLength) {
                            appKey = it.replace(" ", "").toUpperCase(Locale.current)
                        }
                    },
                    label = { Text("AppKey") },
                    modifier = Modifier.width(310.dp))
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    formattedAppKey = formatInputToMSB(appKey)
                    Text(text = "AppKey: $formattedAppKey", style = TextStyle(fontSize = 12.sp))
                }
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
            Button(onClick = {resultText = "JoinEUI: $formatedJoinEUI \nDevEUI: $formatedDevEUI \nAppKEY: $formattedAppKey" }) {
                Text(text = "Eingaben speichern")
            }
            Text(resultText, style = TextStyle(fontSize = 12.sp))

            Button(onClick = {appKey = generateRandomAppKey() }) {
                Text(text = "AppKey generierung")
            }
        }
    }

    private fun formatInputToMSB(input: String): String {
        return input.chunked(2).joinToString(" 0x", prefix = "0x")
    }

    private fun generateRandomAppKey(): String {
        val charPool = (('A'..'Z') + ('0' .. '9'))
        val appKeyList = ArrayList<Char>()

        for(i in 1..appKeyLength) {
            val single = charPool.random()
            appKeyList.add(single)
        }

        val appKeyString = appKeyList.joinToString("")
        return appKeyString
    }

    @Composable
    fun getSensorsButton(onClick: () -> Unit){
        Button(onClick = onClick) {
            Text("Sensoren abfragen")
        }
    }
}