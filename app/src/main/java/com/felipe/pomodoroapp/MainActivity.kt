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
    var seconds by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Segundos: $seconds",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { seconds++ }) {
            Text("Sumar 1 segundo")
        }
    }
}
