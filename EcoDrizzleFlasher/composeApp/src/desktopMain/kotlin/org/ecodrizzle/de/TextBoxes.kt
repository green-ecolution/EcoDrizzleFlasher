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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class EuiInputTextBoxes {
    @Preview
    @Composable
    fun EuiFields() {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.Start) {
            var endDeviceId by remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = endDeviceId,
                    onValueChange = {
                        if(it.length <= 16) {
                            endDeviceId = it
                        }
                    },
                    label = { Text("End-Device-ID") })
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    Text(text = "Device ID: $endDeviceId", style = TextStyle(fontSize = 12.sp))
                }
            }

            var joinEUI by remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = joinEUI,
                    onValueChange = {
                        if(it.length <= 16) {
                            joinEUI = it
                        }
                    },
                    label = { Text("JoinEUI") })
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    Text(text = "JoinEUI: $joinEUI", style = TextStyle(fontSize = 12.sp))
                }
            }

            var devEui by remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = devEui,
                    onValueChange = {
                        if(it.length <= 16) {
                            devEui = it
                        }
                    },
                    label = { Text("DevEUI") })
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    Text(text = "DevEUI: $devEui", style = TextStyle(fontSize = 12.sp))
                }
            }

            var appKey by remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = appKey,
                    onValueChange = {
                        if(it.length <= 32) {
                            appKey = it
                        }
                    },
                    label = { Text("AppKey") },
                    modifier = Modifier.width(310.dp))
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)){
                    Text(text = "AppKey: $appKey", style = TextStyle(fontSize = 12.sp))
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
        }
    }

    @Composable
    fun getSensorsButton(onClick: () -> Unit){
        Button(onClick = onClick) {
            Text("Sensoren abfragen")
        }
    }
}