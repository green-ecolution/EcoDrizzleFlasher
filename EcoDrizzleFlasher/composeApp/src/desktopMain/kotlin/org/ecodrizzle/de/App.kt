package org.ecodrizzle.de

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        val euiInputTextBoxes = EuiInputTextBoxes()
        euiInputTextBoxes.EuiFields()
    }
}

@Composable
@Preview
fun AppPreview() {
    Text("hello Compose")
}