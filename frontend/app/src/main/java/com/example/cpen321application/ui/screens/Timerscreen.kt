package com.example.cpen321application.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import com.example.cpen321application.R
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun Timerscreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var secondsleft by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    val mm = secondsleft / 60
    val ss = secondsleft % 60
    var message by remember { mutableStateOf<String?>(null) }
    var showImage by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(100.dp) }

    LaunchedEffect(secondsleft, running) {
        if (running && secondsleft > 0) {
            delay(1000)
            secondsleft--
        }
        else if (running && secondsleft == 0) {
            for (i in 3 downTo 1) {
                message = "Jumpscare in $i..."
                delay(1000)
            }
            message = "Error: preview.png failed"
            delay(2000)
            message = null
            repeat(3) { i ->
                imageSize = (100 + i * 150).dp
                showImage = true
                delay(200)
                showImage = false
                delay(200)
            }
            running = false
        }
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
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%02d:%02d".format(mm, ss)
                )
                message?.let {
                    Text(it, style = MaterialTheme.typography.headlineMedium)
                }
                if (showImage) {
                    Image(
                        painter = painterResource(R.drawable.preview),
                        contentDescription = "Jumpscare",
                        modifier = Modifier.size(imageSize)
                    )
                }
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Minutes") }
                )
                OutlinedTextField(
                    value = seconds,
                    onValueChange = { seconds = it },
                    label = { Text("Seconds") }
                )

                Button(
                    onClick = {
                        if (!running) {
                            val mins = minutes.toIntOrNull() ?: 0
                            val secs = seconds.toIntOrNull() ?: 0
                            val total = (mins * 60) + secs
                            if (total > 0) {
                                message = null
                                showImage = false
                                secondsleft = total
                                running = true
                            }
                        } else {
                            secondsleft = 0
                            running = false
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (!running && secondsleft == 0) "Start Timer" else "Reset")
                }
                Button(
                    onClick = { running = !running },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (running) "Pause" else "Resume")
                }

            }
        }
    }
}