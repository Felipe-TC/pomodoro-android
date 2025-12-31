package com.felipe.pomodoroapp

import android.os.Bundle
import android.util.Log
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

        // ======== PRUEBA DE TU CLASE Fraccion ========
        // Esto se ejecuta ANTES de crear la interfaz
        try {
            Log.d("FRACCION_TEST", "=== Iniciando pruebas ===")

            // Test 1: Crear fracciones
            val f1 = Fraccion(1, 2)
            val f2 = Fraccion(1, 3)
            Log.d("FRACCION_TEST", "f1 = ${f1.toString()}")
            Log.d("FRACCION_TEST", "f2 = ${f2.toString()}")

            // Test 2: Sumar
            val suma = f1.suma(f2)
            Log.d("FRACCION_TEST", "f1 + f2 = ${suma.toString()}")

            // Test 3: Comparar
            val esMayor = f1.mayorQue(f2)
            Log.d("FRACCION_TEST", "f1 > f2? $esMayor")

            // Test 4: Desde String (opcional)
            val f3 = Fraccion.desdeString("3/4")
            Log.d("FRACCION_TEST", "Desde string '3/4' = ${f3.toString()}")

            // Test 5: Error (denominador cero)
            try {
                val fError = Fraccion(1, 0)
            } catch (e: IllegalArgumentException) {
                Log.d("FRACCION_TEST", "Correcto! atrapo el error: ${e.message}")
            }

            Log.d("FRACCION_TEST", "=== Pruebas completadas ===")
        } catch (e: Exception) {
            Log.e("FRACCION_TEST", "Error: ${e.message}")
        }
        // ======== FIN DE PRUEBAS ========


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

