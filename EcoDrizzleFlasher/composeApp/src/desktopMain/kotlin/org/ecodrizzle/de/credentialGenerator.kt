package org.ecodrizzle.de

var TextBoxesVariables = EuiInputTextBoxes()
val charPool = (('A'..'Z') + ('0' .. '9'))

fun generateRandomCredentials(credentialType: String): String {
    val length = when (credentialType) {
        "appKey" -> TextBoxesVariables.appKeyLength
        "devEui" -> TextBoxesVariables.devEuiLength
        else -> throw IllegalArgumentException("Invalid credential type: $credentialType")
    }

    return (1..length).map{ charPool.random() }.joinToString("")
}

