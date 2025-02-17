package org.ecodrizzle.de

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.*
import java.io.File

class FlashComponent (val credentials: Credentials) {
    val pathToCli = "arduino-cli.exe"
    val pathToSketch = "LoRaWan/LoRaWan.ino"

    fun flashEcoDrizzler() {
        val comPort = getComPort()

        if (comPort != null) {
            try {
                writeIds()
                val loadingJob = startSpinner("Flashing EcoDrizzler...")
                runCommand(listOf("cmd", "/c", pathToCli, "config", "set", "library.enable_unsafe_install", "true"))
                runCommand(listOf("cmd", "/c", pathToCli, "lib", "install", "--git-url", "https://github.com/HelTecAutomation/Heltec_ESP32.git"))
                runCommand(listOf("cmd", "/c", pathToCli, "lib", "install", "--git-url", "https://github.com/mikalhart/TinyGPSPlus.git"))
                runCommand(listOf("cmd", "/c", pathToCli, "lib", "install", "Adafruit GFX Library"))
                runCommand(listOf("cmd","/c", pathToCli, "compile", "-p", comPort, "--fqbn", "esp32:esp32:heltec_wifi_lora_32_V3", "--upload", pathToSketch))
                loadingJob.cancel()
                println("Arduino successfully flashed")
            }catch (e: Exception){
                println(e.localizedMessage)
            }

        }
    }

    private fun writeIds(){
        val sketchFile = File(pathToSketch)
        if (!sketchFile.exists()) {
            println("Error: File not found!")
            return
        }

        val joinComma = addCommasToHexString(credentials.joinEui)
        val devComma = addCommasToHexString(credentials.devEui)
        val appKeyComma = addCommasToHexString(credentials.appKey)

        try{
            val updatedSketch = sketchFile.readText()
                .replace(Regex("""uint8_t devEui\[\] = \{[^}]*\};"""), """uint8_t devEui[] = { $devComma };""")
                .replace(Regex("""uint8_t appEui\[\] = \{[^}]*\};"""), """uint8_t appEui[] = { $joinComma };""")
                .replace(Regex("""uint8_t appKey\[\] = \{[^}]*\};"""), """uint8_t appKey[] = { $appKeyComma };""")

            sketchFile.writeText(updatedSketch)

            println("Arduino sketch updated successfully!")
        }catch (e:Exception){
            println("Error: ${e.localizedMessage}")
        }

    }

    private fun getComPort():String?{
        try {
            val comOutput = runCommand(listOf("cmd","/c", pathToCli, "board", "list"))
            val comPort = extractComPort(comOutput)
            return comPort
        }catch (e:Exception){
            println("Error: ${e.localizedMessage}")
            return null
        }
    }

    private fun runCommand(command: List<String>): String {
        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true) // Merge error stream with output
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor() // Wait for process to complete
            output
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun addCommasToHexString(input: String): String {
        return input.split(" ").joinToString(", ")
    }

    private fun extractComPort(output: String): String? {
        val regex = Regex("^(COM\\d+)", RegexOption.MULTILINE) // Match "COM" followed by a number
        return regex.find(output)?.value // Return the first match or null if not found
    }

    private fun startSpinner(message: String): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            val spinnerChars = listOf("|", "/", "-", "\\")
            var index = 0
            while (isActive) {
                print("\r$message ${spinnerChars[index]}") // Carriage return keeps it on one line
                index = (index + 1) % spinnerChars.size
                delay(100) // Controls spinner speed
            }
        }
    }
}