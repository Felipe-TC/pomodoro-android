package com.felipe.pomodoroapp

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    PomodoroApp()
                }
            }
        }
    }
}

// ===== NUEVO: ENUM para los estados del Pomodoro =====
enum class PomodoroState {
    WORK,           // Trabajo (25 min)
    SHORT_BREAK,    // Descanso corto (5 min)
    LONG_BREAK      // Descanso largo (15 min)
}

// ===== NUEVO: Clase que maneja la logica del Pomodoro =====
class PomodoroTimer {
    // Tiempos en segundos
    val trabajoMinutos          = 25
    val descansoCortoMinutos    = 5
    val descansoLargoMinutos    = 15

    // Convertir a segundos
    val tiempoTrabajoSegundos       = trabajoMinutos * 60
    val tiempoDescnsoCortoSegundos  = descansoCortoMinutos * 60
    val tiempoDescansoLargoSegundos = descansoLargoMinutos * 60

    // ===== VARIABLES DE ESTADO (var = pueden cambiar) =====
    var estadoActual by mutableStateOf(PomodoroState.WORK)
    var tiempoRestante by mutableStateOf(tiempoTrabajoSegundos)
    var estaCorriendo by mutableStateOf(false)
    var sesionesCompletadas by mutableStateOf(0)

    // Contador interno para saber cuando dar descanso largo
    val sesionesAntesDescansoLargo  = 4
    var contadorSesionesTrabajo     = 0

    // ===== METODO: Cuando termina el tiempo =====
    private fun cuandoTerminaTiempo() {
        // 1. Detener el temporizador
        estaCorriendo = false

        // 2. Dependiendo del estado actual, hacer algo diferente
        when (estadoActual) {
            PomodoroState.WORK -> {
                manejarFinTrabajo()
            }

            PomodoroState.SHORT_BREAK -> {
                manejarFinDescansoCorto()
            }

            PomodoroState.LONG_BREAK -> {
                manejarFinDescansoLargo()
            }
        }

        // 3. Aqui despues agregaremos sonido
        println("Tiempo terminado! Estado: $estadoActual")
    }

    // ===== METODO: Manejar cuando termina tiempo de trabajo =====
    private fun manejarFinTrabajo() {
        // Aumentar contadores
        sesionesCompletadas     = sesionesCompletadas + 1
        contadorSesionesTrabajo = contadorSesionesTrabajo + 1

        //Decidir que tipo de descanso toca
        val tocaDescansoLargo = contadorSesionesTrabajo >= sesionesAntesDescansoLargo

        if (tocaDescansoLargo) {
            estadoActual = PomodoroState.LONG_BREAK
            contadorSesionesTrabajo = 0     // Reiniciar contador
        } else {
            estadoActual = PomodoroState.SHORT_BREAK
        }

        // Actualizar tiempo para el nuevo estado
        actualizarTiempoParaEstadoActual()
    }

    // ===== METODO: Manejar cuando termina descanso corto =====
    private fun manejarFinDescansoCorto() {
        estadoActual = PomodoroState.WORK
        actualizarTiempoParaEstadoActual()
    }

    // ===== METODO: Manejar cuando termina descanso largo =====
    private fun manejarFinDescansoLargo() {
        estadoActual = PomodoroState.WORK
        actualizarTiempoParaEstadoActual()
    }

    // ===== METODO: Actualizar tiempo segun estado =====
    private fun actualizarTiempoParaEstadoActual() {
        tiempoRestante = when(estadoActual) {
            PomodoroState.WORK -> tiempoTrabajoSegundos
            PomodoroState.SHORT_BREAK -> tiempoDescnsoCortoSegundos
            PomodoroState.LONG_BREAK -> tiempoDescansoLargoSegundos
        }
    }

    // ===== METODO: Iniciar o pausar =====
    fun alternarTemporizador() {
        estaCorriendo = !estaCorriendo
    }

    // ===== METODO: Reiniciar tiempo actual =====
    fun reiniciarTemporizador() {
        estaCorriendo = false
        actualizarTiempoParaEstadoActual()
    }

    // ===== METODO: Cambiar a trabajo =====
    fun cambiarATrabajo() {
        estadoActual = PomodoroState.WORK
        reiniciarTemporizador()
    }

    // ===== METODO: Cambiar a descanso corto =====
    fun cambiarADescansoCorto() {
        estadoActual = PomodoroState.SHORT_BREAK
        reiniciarTemporizador()
    }

    // ===== METODO: Cambiar a descanso largo =====
    fun cambiarADescansoLargo() {
        estadoActual = PomodoroState.LONG_BREAK
        reiniciarTemporizador()
    }

    // ===== METODO: Actualizar tiempo (se llama cada segundo) =====
    fun actualizarTiempo() {
        if (estaCorriendo && tiempoRestante >0) {
            // Reducir un segundo
            tiempoRestante = tiempoRestante - 1

            // Si llego a cero, manejar fin
            if (tiempoRestante == 0) {
                cuandoTerminaTiempo()
            }
        }
    }

    // ===== METODO: Reiniciar TODO (contadores tambien) =====
    fun reiniciarTodo() {
        estaCorriendo   = false
        estadoActual    = PomodoroState.WORK
        sesionesCompletadas = 0
        contadorSesionesTrabajo = 0
        actualizarTiempoParaEstadoActual()
    }
}

// ===== APP PRINCIPAL =====
@Composable
fun PomodoroApp() {
    val pomodoroTimer = remember { PomodoroTimer() }

    // Esto actualiza el tiempo cada segundo
    LaunchedEffect(pomodoroTimer.estaCorriendo) {
        while (true) {
            delay(1000L)
            if (pomodoroTimer.estaCorriendo) {
                pomodoroTimer.actualizarTiempo()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar estado actual
        Text(
            text = when (pomodoroTimer.currentState) {
                PomodoroState.WORK -> "⏰ TRABAJO"
                PomodoroState.SHORT_BREAK -> "☕ DESCANSO CORTO"
                PomodoroState.LONG_BREAK -> "🌴 DESCANSO LARGO"
            },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contador de sesiones
        Text(
            text = "Pomodoros completados: ${pomodoroTimer.completedSessions}",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Temporizador (formato MM:SS)
        val minutes = pomodoroTimer.timeLeft / 60
        val seconds = pomodoroTimer.timeLeft % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        Text(
            text = timeFormatted,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ===== BOTONES PRINCIPALES =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Boton Start/Pause
            Button(
                onClick = { pomodoroTimer.toggleTimer() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pomodoroTimer.isRunning) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = if (pomodoroTimer.isRunning) "⏸\uFE0F PAUSA" else "▶\uFE0F INICIAR",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Boton Reset
            Button(
                onClick = { pomodoroTimer.resetTimer() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("\uD83D\uDD04 REINICIAR", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ===== BOTONES PARA CAMBIAR MODO =====
        Text("Cambiar modo:", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Boton Trabajo
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.WORK,
                onClick = { pomodoroTimer.switchToWork() },
                label = { Text("Trabajo (25 min)") }
            )

            // Boton Descanso Corto
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.SHORT_BREAK,
                onClick = { pomodoroTimer.switchToShortBreak() },
                label = { Text("Descanso (5 min)") }
            )

            // Boton Descanso Largo
            FilterChip(
                selected = pomodoroTimer.currentState == PomodoroState.LONG_BREAK,
                onClick = { pomodoroTimer.switchToLongBreak() },
                label = { Text("Descanso (15 min)") }
            )
        }
    }
}