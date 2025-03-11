package org.ecodrizzle.de

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val deviceId: String, val devEui: String, val joinEui: String, val appKey: String, var dutyCycle: Int = 24)