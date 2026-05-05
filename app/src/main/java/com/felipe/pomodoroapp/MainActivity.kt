package com.felipe.pomodoroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipe.pomodoroapp.ui.PantallaConfiguracion
import com.felipe.pomodoroapp.ui.PantallaPomodoro
import com.felipe.pomodoroapp.ui.theme.PomodoroAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "pomodoro"
    ) {
        composable("pomodoro") {
            PantallaPomodoro(
                onNavigateToConfiguracion = {
                    navController.navigate("configuracion")
                }
            )
        }
        composable("configuracion") {
            PantallaConfiguracion(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}