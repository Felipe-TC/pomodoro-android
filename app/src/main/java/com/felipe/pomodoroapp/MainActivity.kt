package com.felipe.pomodoroapp

import com.felipe.pomodoroapp.ui.PantallaPomodoro  // ← Import correcto
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.felipe.pomodoroapp.ui.PantallaPomodoro
import com.felipe.pomodoroapp.ui.theme.PomodoroAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    PantallaPomodoro()
                }
            }
        }
    }
}