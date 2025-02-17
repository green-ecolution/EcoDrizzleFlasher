package org.ecodrizzle.de

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import java.io.File

class FlashComponent (val credentials: Credentials) {
    val pathToCli = "arduino-cli.exe"
    val pathToSketch = "LoRaWan/LoRaWan.ino"

    @Composable
    fun flash(){
        Button(
            onClick = {
                try {
                    flashEcoDrizzler()
                }catch (e: Exception){
                    println(e.localizedMessage)
                }

            }
        ){
            Text("List")
        }
    }

    private fun flashEcoDrizzler() {
        val comPort = getComPort()
        println(comPort)
        if (comPort != null) {
            writeIds()
            runCommand(listOf("cmd","/c", pathToCli, "compile", "-p", comPort, "--fqbn", "esp32:esp32:heltec_wifi_lora_32_V3", "--upload", pathToSketch))
            println("Arduino successfully flashed")
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
        println(joinComma)
        println(devComma)
        println(appKeyComma)


        val updatedSketch = sketchFile.readText()
            .replace(Regex("""uint8_t devEui\[\] = \{[^}]*\};"""), """uint8_t devEui[] = { $devComma };""")
            .replace(Regex("""uint8_t appEui\[\] = \{[^}]*\};"""), """uint8_t appEui[] = { $joinComma };""")
            .replace(Regex("""uint8_t appKey\[\] = \{[^}]*\};"""), """uint8_t appKey[] = { $appKeyComma };""")

        sketchFile.writeText(updatedSketch)

        println("Arduino sketch updated successfully!")
    }

    private fun getComPort():String?{
        val comOutput = runCommand(listOf("cmd","/c", pathToCli, "board", "list"))
        val comPort = extractComPort(comOutput)
        return  comPort
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
}