package org.ecodrizzle.de

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*
import java.io.File
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class FlashComponent (val credentials: Credentials) {
    val pathToCli = "arduino-cli.exe"
    val owner = "green-ecolution"
    val repo = "green-ecolution-ESP32"
    val folderPath = "eco_drizzle"
    val branch = "?ref=main"
    val outputDirectory = "./eco_drizzle"
    val pathToSketch = "$folderPath/$folderPath.ino"
    val secrets = "$folderPath/secrets_template.h"

    suspend fun downloadSketch(){
        File(outputDirectory).mkdirs()
        downloadFolder(owner, repo, folderPath, outputDirectory)
        println("Download complete! Files saved in: $outputDirectory")
    }
    suspend fun downloadFolder(owner: String, repo: String, path: String, outputDir: String) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val url = "https://api.github.com/repos/$owner/$repo/contents/$path$branch"
        println("Fetching: $url")

        val response: String = client.get(url).body()
        val jsonElement = Json.parseToJsonElement(response)

        when (jsonElement) {
            is JsonArray -> {
                // ✅ Normal case: folder contains multiple files/subfolders
                for (file in jsonElement) {
                    val fileUrl = file.jsonObject["download_url"]?.jsonPrimitive?.content
                    val fileName = file.jsonObject["name"]?.jsonPrimitive?.content
                    val fileType = file.jsonObject["type"]?.jsonPrimitive?.content

                    if (fileType == "file" && fileUrl != null && fileName != null) {
                        downloadFile(client, fileUrl, "$outputDir/$fileName")
                    } else if (fileType == "dir") {
                        // Recursively download subfolders
                        val subfolderPath = "$path/$fileName"
                        val subfolderOutput = "$outputDir/$fileName"
                        File(subfolderOutput).mkdirs()
                        downloadFolder(owner, repo, subfolderPath, subfolderOutput)
                    }
                }
            }
            is JsonObject -> {
                // ❌ Error case or the path is a file instead of a folder
                if (jsonElement.containsKey("message")) {
                    println("⚠️ GitHub API Error: ${jsonElement["message"]}")
                } else {
                    println("⚠️ Expected a folder, but got a file: $path")
                }
            }
            else -> {
                println("⚠️ Unexpected JSON format received!")
            }
        }

        client.close()
    }

    suspend fun downloadFile(client: HttpClient, fileUrl: String, outputPath: String) {
        val response: HttpResponse = client.get(fileUrl)
        val file = File(outputPath)
        file.parentFile?.mkdirs()
        file.writeBytes(response.body())
        println("files should be written")
    }

    fun flashEcoDrizzler(): Boolean {
        runBlocking {
            downloadSketch()
        }
        println("weiter")
        val comPort = getComPort() ?: return false
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
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return true
    }

    private fun writeIds(){
        val sketchFile = File(secrets)
        if (!sketchFile.exists()) {
            println("Error: File not found!")
            return
        }

        val joinComma = addCommasToHexString(credentials.joinEui)
        val devComma = addCommasToHexString(credentials.devEui)
        val appKeyComma = addCommasToHexString(credentials.appKey)

        try{
            val updatedSketch = sketchFile.readText()
                .replace(Regex("""const char deviceName\[\] = "([^"]*)";"""), """const char deviceName[] = "${credentials.deviceId}";""")
                .replace(Regex("""uint8_t devEui\[\] = \{[^}]*\};"""), """uint8_t devEui[] = { $devComma };""")
                .replace(Regex("""uint8_t appEui\[\] = \{[^}]*\};"""), """uint8_t appEui[] = { $joinComma };""")
                .replace(Regex("""uint8_t appKey\[\] = \{[^}]*\};"""), """uint8_t appKey[] = { $appKeyComma };""")

            sketchFile.writeText(updatedSketch)
            copySecretsFile()
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
    fun copySecretsFile() {
        val sourceFile = File(secrets)
        val destinationFile = File("$outputDirectory/secrets.h")
        if (sourceFile.exists()) {
            sourceFile.copyTo(destinationFile, overwrite = true)
            println("✅ File copied successfully!")
        } else {
            println("⚠️ Source file does not exist.")
        }
    }
}