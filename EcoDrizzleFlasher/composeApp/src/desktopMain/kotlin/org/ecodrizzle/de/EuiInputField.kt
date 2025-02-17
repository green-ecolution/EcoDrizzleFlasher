package org.ecodrizzle.de

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class EuiInputField {
    @Composable
    fun inputField(inputFieldName : String,
                   inputLength : Int,
                   add0x : Boolean,
                   onlyDigits : Boolean,
                   toUpperCase : Boolean,
                   randomGeneration : Boolean,
                   onValueChange: (String) -> Unit
    ){
        var userInput by remember { mutableStateOf("") }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                singleLine = true,
                value = userInput,
                onValueChange = { it ->
                    val onlyDigitsFilter = it.filter { it.isDigit() }
                    when {
                        toUpperCase && it.length <= inputLength->
                            userInput = it.toUpperCase(Locale.current)
                        onlyDigits && it.length <= inputLength ->
                            userInput = onlyDigitsFilter
                        it.length <= inputLength ->
                            userInput = it.replace(" ", "-")
                    }
                    if (add0x) onValueChange(formatInputToMSB(userInput)) else onValueChange(userInput)
                },
                label = {Text(inputFieldName)})
            Spacer(Modifier.width(12.dp))
            if(randomGeneration) {
                Button(
                    onClick = {
                        userInput = generateRandomCredentials(inputLength)
                        onValueChange(formatInputToMSB(userInput))
                    },
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "$inputFieldName-Generator-Button",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = inputFieldName, style = TextStyle(fontSize = 15.sp))
                }
            }
            Box(modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically)) {
                val displayedText = if (add0x) formatInputToMSB(userInput) else userInput
                Text(text = "$inputFieldName: $displayedText", style = TextStyle(fontSize = 12.sp))
            }
        }
    }

    private fun formatInputToMSB(input: String): String {
        return input.chunked(2).joinToString(" 0x", prefix = "0x")
    }
}