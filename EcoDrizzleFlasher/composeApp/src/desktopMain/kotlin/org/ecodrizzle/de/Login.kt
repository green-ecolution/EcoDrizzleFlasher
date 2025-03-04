
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.ecodrizzle.de.EuiInputTextBoxes


fun openBrowser(url: String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI(url))
    } else {
        println("Desktop browsing not supported")
    }
}


fun startLocalServer(onCodeReceived: (String) -> Unit) {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/callback") {
                val code = call.request.queryParameters["code"]
                if (code != null) {
                    onCodeReceived(code)
                    call.respondText("Login successful! You can close this window.")
                } else {
                    call.respondText("Invalid request")
                }
            }
        }
    }.start(wait = false)
}

suspend fun getUser(keyCloakUrl: String, accessToken:Tokens):String? {
    return accessToken.let { token ->
        println(token)
        val userInfoUrl = "$keyCloakUrl/userinfo"
        val response: HttpResponse = HttpClient().get(userInfoUrl) {
            headers {
                append("Authorization", "Bearer ${token.access_token}")
            }
        }
        //println(response.bodyAsText())
        val userInfo = Json.decodeFromString<UserInfo>(response.bodyAsText())
        //println(userInfo)
        userInfo.ttn // Replace "apiKey" with the actual field name
    }
}

@Composable
fun login(){
    val scope = rememberCoroutineScope()
    var loginSuccess by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf<String?>(null) }
    val keycloakUrl = "https://auth.green-ecolution.de/realms/green-ecolution-dev/protocol/openid-connect"
    val clientId = "flasher-client"
    val redirectUri = "http://localhost:8080/callback" // Or a custom protocol like myapp://callback

    // Start the local server to handle the redirect
    LaunchedEffect(Unit) {
        startLocalServer { code ->
            scope.launch {
                val tokens = exchangeCodeForTokens(code, clientId, redirectUri, keycloakUrl)
                //println(tokens)
                if(tokens != null) {
                    apiKey = getUser(keycloakUrl, tokens)
                    //println(apiKey)
                    if(apiKey != null) {
                        loginSuccess = true
                    }
                }
            }
        }
    }
        if (!loginSuccess) {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    val loginUrl =
                        "$keycloakUrl/auth?response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=openid"
                    //println(loginUrl)
                    openBrowser(loginUrl)
                }) {
                    Text("Login")
                }
            }
        }else{
            val euiInputTextBoxes = EuiInputTextBoxes()
            println(apiKey)
            euiInputTextBoxes.EuiFields(apiKey!!)
        }

}

@OptIn(InternalAPI::class)
private suspend fun exchangeCodeForTokens(code: String, clientId: String, redirectUri: String, keyCloakUrl:String): Tokens? {
    val tokenUrl = "$keyCloakUrl/token"
    val clientSecret = "<your-client-secret>" // Only for confidential clients

    return try {
        val response: HttpResponse = HttpClient().post(tokenUrl) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            body = FormDataContent(Parameters.build {
                append("grant_type", "authorization_code")
                append("code", code)
                append("client_id", clientId)
                append("client_secret", clientSecret) // Only for confidential clients
                append("redirect_uri", redirectUri)
            })
        }
        Json.decodeFromString<Tokens>(response.bodyAsText())
    } catch (e: Exception) {
        println("Error exchanging code for tokens: ${e.message}")
        null
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class Tokens(
    val access_token: String,
    val refresh_token: String,
    val id_token: String,
    val expires_in: Int,
    val token_type: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UserInfo(
    val preferred_username: String,
    val sub: String,
    val email: String,
    val ttn: String
)