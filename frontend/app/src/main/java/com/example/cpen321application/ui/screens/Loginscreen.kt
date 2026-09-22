package com.example.cpen321application.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cpen321application.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private suspend fun getJson(url: String): JSONObject = withContext(Dispatchers.IO) {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.connectTimeout = 5000
    conn.readTimeout = 5000
    conn.inputStream.bufferedReader().use { JSONObject(it.readText()) }
}

private fun clientIp(): String =
    NetworkInterface.getNetworkInterfaces().toList()
        .flatMap { it.inetAddresses.toList() }
        .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
        ?.hostAddress ?: "unknown"

@Composable
fun Loginscreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var info by remember { mutableStateOf("Please sign in with Google.") }
    val base = BuildConfig.API_BASE_URL.trimEnd('/')
    var signedIn by remember { mutableStateOf(false) }

    fun signInAndLoad() {
        scope.launch {
            info = "Signing in..."
            try {
                val option = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .build()
                val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                val result = CredentialManager.create(context).getCredential(context, request)
                val google = GoogleIdTokenCredential.createFrom(result.credential.data)
                val userName = "${google.givenName ?: ""} ${google.familyName ?: ""}".trim()
                signedIn = true
                info = "Loading server info..."
                val ip = getJson("$base/server-ip").getString("ip")
                val time = getJson("$base/server-time").getString("time")
                val name = getJson("$base/name")
                val clientTime = ZonedDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss 'GMT'xxx"))

                info = """
                    Server IP address:
                    $ip
                    
                    Client IP address:
                    ${clientIp()}
                    Server local time:
                    $time
                    Client local time:
                    $clientTime
                    Server owner:
                    ${name.getString("firstName")} ${name.getString("lastName")}
                    Signed-in user:
                    $userName
                """.trimIndent()
            } catch (e: Exception) {
                info = "Error: ${e.message}"
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        TextButton(onClick = onBack) { Text("← Back") }
        Column(modifier = Modifier.padding(24.dp)) {
            if (!signedIn) {
                Button(onClick = { signInAndLoad() }) { Text("Sign in with Google") }
                Spacer(Modifier.height(16.dp))
            }
            Text(info, style = MaterialTheme.typography.bodyLarge)
        }
    }
}