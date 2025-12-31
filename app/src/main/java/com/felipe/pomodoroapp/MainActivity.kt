package com.felipe.pomodoroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SimpleTimer()
                }
            }
        }
    }
}

@Composable
fun SimpleTimer() {
    val totalTime = 25 * 60 // 25 minutos en segundos
    var timeLeft by remember { mutableStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = timeFormatted,
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isRunning = true },
            enabled = !isRunning && timeLeft > 0
        ) {
            Text("Start")
        }
    }
}

