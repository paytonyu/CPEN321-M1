package com.example.cpen321application.ui.screens
import android.util.Log
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

@Composable
fun Timerscreen(modifier: Modifier = Modifier) {
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var secondsleft by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    val mm = secondsleft / 60
    val ss = secondsleft % 60

    LaunchedEffect(secondsleft, running) {
        if (running && secondsleft > 0) {
            delay(1000)
            secondsleft--
        }
        else if (running && secondsleft == 0) {
            running = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%02d:%02d".format(mm, ss)
        )
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
                    // Start Timer Logic
                    val mins = minutes.toIntOrNull() ?: 0
                    val secs = seconds.toIntOrNull() ?: 0
                    secondsleft = (mins * 60) + secs
                    running = true
                } else {
                    // Fix: Directly reset the actual state variables
                    secondsleft = 0
                    running = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (!running && secondsleft == 0) "Start Timer" else "Reset")
        }
        Button(
            onClick = {running = !running},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (running) "Pause" else "Resume")
        }

    }
}