package com.example.cpen321application.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cpen321application.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement

@Composable
fun LiveUpdateScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val pixels = remember { mutableStateListOf<Color>().apply { repeat(256) { add(Color.White) } } }

    DisposableEffect(Unit) {
        val url = BuildConfig.API_BASE_URL.replace("http", "ws") + "/live"
        val request = Request.Builder().url(url).build()
        val handler = Handler(Looper.getMainLooper())
        var lastTime = 0L

        val socket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val x = json.getInt("x")
                val y = json.getInt("y")
                val color = Color(android.graphics.Color.parseColor(json.getString("color")))

                handler.post {
                    val now = System.currentTimeMillis()
                    if (now - lastTime > 3000) {
                        for (i in 0 until 256) pixels[i] = Color.White
                    }
                    lastTime = now
                    pixels[y * 16 + x] = color
                }
            }
        })

        onDispose { socket.close(1000, null) }
    }
    Column(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pixel Art")
                for (row in 0 until 16) {
                    Row {
                        for (col in 0 until 16) {
                            Box(Modifier.size(20.dp).background(pixels[row * 16 + col]))
                        }
                    }
                }
            }
        }
    }
}