package com.felipe.pomodoroapp.ui

import androidx.compose.runtime.getValue   // ← Agregar
import androidx.compose.runtime.setValue   // ← Agregar
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.pomodoroapp.logic.LogicaPomodoro
import com.felipe.pomodoroapp.model.EstadoPomodoro
import com.felipe.pomodoroapp.utils.formateoTiempo
import kotlinx.coroutines.delay
import com.felipe.pomodoroapp.R

// ===== PANTALLA PRINCIPAL =====
@Composable
fun PantallaPomodoro() {
    // ===== NUEVO: Obtener el contexto de Android =====
    // Necesario para crear el MediaPlayer que reproduce sonido
    val contextoAndroid: Context = LocalContext.current

    // 1. CREAR INSTANCIA de la logica
    // remember: Guarda el objeto entre re-dibujados de la pantalla
    val logicaPomodoro = remember { LogicaPomodoro() }


    // 2. CREAR VARIABLES DE ESTADO para la UI
    // Estas son variables ESPECIALES que cuando cambian, la pantalla se re-dibuja
    var tiempoRestanteUI by remember { mutableStateOf(logicaPomodoro.tiempoRestanteSegundos) }
    var estaCorriendoUI by remember { mutableStateOf(logicaPomodoro.estaCorriendo) }
    var estadoActualUI by remember { mutableStateOf<EstadoPomodoro>(logicaPomodoro.estadoActual) }
    var sesionesCompletadasUI by remember { mutableStateOf(logicaPomodoro.sesionesCompletadas) }

    // ===== NUEVO: Variable para el evento de tiempo terminado =====
    var tiempoTerminadoEventoUI by remember { mutableStateOf(logicaPomodoro.tiempoTerminadoEvento) }

    // ===== NUEVO: Crear el reproductor de sonido =====
    val reproductorSonido = remember {
        // Intentar crear el MediaPlayer
        try {
            // MediaPlayer.create() puede devolver null si hay error
            val reproductorTemporal = MediaPlayer.create(
                contextoAndroid,
                R.raw.ahhputaquericoeh
            )

            if (reproductorTemporal == null) {
                println("ADVERTENCIA: No se puede crear el MediaPlayer. Sonido desactivado.")
                null    // Devolver null para indicar el error
            } else {
                reproductorTemporal  // Devolver el reproductor creado
            }
        } catch (excepcion: Exception) {
            println("ERROR al crear el MediaPlayer: ${excepcion.message}")
            null    // Devolver null en caso de excepcion
        }
    }

    // 3. SINCRONIZAR LOGICA CON UI (cada segundo)
    // MODIFICAR este bloque para agregar manejo de sonido
    // LaunchedEffect: Ejecuta codigo en un "hilo" separado
    LaunchedEffect(estaCorriendoUI) {
        // Este while se ejecuta mientras la pantalla este visible
        while (true) {
            delay(1000L)    // Esperar 1000 milisegundos = 1 segundo

            if (estaCorriendoUI) {
                // Actualizar la logica
                logicaPomodoro.actualizarTiempo()

                // Sincronizar todas la variables con la UI
                tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos
                estadoActualUI = logicaPomodoro.estadoActual
                sesionesCompletadasUI = logicaPomodoro.sesionesCompletadas

                // ===== NUEVO: Verificar si ocurrio el evento de tiempo terminado =====
                val ocurrioEventoEnLogica = logicaPomodoro.tiempoTerminadoEvento
                val eventoNoEstaMostradoEnUI = !tiempoTerminadoEventoUI

                if (ocurrioEventoEnLogica && eventoNoEstaMostradoEnUI) {
                    // Actualizar la UI para mostrar que ocurrió el evento
                    tiempoTerminadoEventoUI = true

                    // ===== REPRODUCIR SONIDO =====
                    try {
                        // Verificar si tenemos un reproductor valido
                        if (reproductorSonido != null) {
                            // Verificar si el reproductor ya esta sonando
                            val reproductorEstaSonando = reproductorSonido.isPlaying

                            if (reproductorEstaSonando) {
                                // Si ya esta sonando, iniciar la reproduccion
                                reproductorSonido.seekTo(0)
                            } else {
                                reproductorSonido.start()
                            }

                            println("DEBUG: Sonido reproducido correctamente")
                        } else {
                            println("ADVERTENCIA: Reproductor no disponible. No se reproduce sonido")
                        }
                    } catch (excepcion: Exception) {
                        // En caso de error, mostrar en consola pero no bloquear la app
                        println("ERROR al reproducir sonido: ${excepcion.message}")
                    }

                    // ===== IMPORTANTE: Resetear el evento en la logica =====
                    logicaPomodoro.resetearEventoTiempoTerminado()
                }
            }
        }
    }

    // ===== NUEVO: Limpiar recursos cuando la pantalla desaparece =====
    // Esto evita "fugas de mmeoria" (memory leaks)
    DisposableEffect(Unit) {
        // Este codigo se ejecuta cuando la pantalla se va a eliminar
        onDispose {
            // Liberar el reproductor de sonido solo si no es null
            if (reproductorSonido != null) {
                reproductorSonido.release()
                println("DEBUG: Reproductor de sonido liberado correctamente")
            } else {
                println("DEBUG: No hay reproductor que liberar")
            }
        }
    }

    // 4. CALCULAR VALORES para mostrar
    val tiempoFormateado = formateoTiempo(tiempoRestanteUI)

    val nombreEstado = when (estadoActualUI) {
        EstadoPomodoro.TRABAJO -> "TRABAJO"
        EstadoPomodoro.DESCANSO_CORTO -> "DESCANSO CORTO"
        EstadoPomodoro.DESCANSO_LARGO -> "DESCANSO LARGO"
        else -> "DESCONOCIDO"
    }

    // ===== NUEVO: Crear mensaje para mostrar cuando el tiempo termina =====
    val mensajeTiempoTerminado = if (tiempoTerminadoEventoUI) {
        "¡TIEMPO TERMINADO! \uD83D\uDD0A"
    } else {
        ""
    }

    // 5. CREAR LA INTERFAZ (UI) - MODIFICADA para mostrar el mensaje
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titulo del estado (se mantiene igual)
        Text(
            text = nombreEstado,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // ===== NUEVO: Mostrar mensaje cuando el tiempo termina =====
        if (tiempoTerminadoEventoUI) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensajeTiempoTerminado,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error,
                fontStyle = FontStyle.Italic
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contador de sesiones (se mantiene igual)
        Text(
            text = "Pomodoros completados: $sesionesCompletadasUI",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Temporizador grande (se mantiene igual)
        Text(
            text = tiempoFormateado,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ===== BOTONES PRINCIPALES =====
        // MODIFICAR los onClick para resetear el mensaje de tiempo terminado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Boton Iniciar/Pausar - MODIFICADO
            Button(
                onClick = {
                    // 1. Llamar a la logica
                    logicaPomodoro.alternarTemporizador()

                    // 2. Sincronizar con UI
                    estaCorriendoUI = logicaPomodoro.estaCorriendo
                    tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos

                    // ===== NUEVO: Ocultar mensaje de tiempo temrinado si esta visible =====
                    if (tiempoTerminadoEventoUI) {
                        tiempoTerminadoEventoUI = false
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (estaCorriendoUI) "⏸️ PAUSAR" else "▶️ INICIAR",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Boton Reiniciar - MODIFICADO
            Button(
                onClick = {
                    logicaPomodoro.reiniciarTemporizadorActual()
                    estaCorriendoUI = logicaPomodoro.estaCorriendo
                    tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos

                    // ===== NUEVO: Ocultar mensaje de tiempo temrinado =====
                    tiempoTerminadoEventoUI = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("🔄 REINICIAR", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Boton para reiniciar TODO - MODIFICADO
        TextButton(
            onClick = {
                logicaPomodoro.reiniciarTodo()
                estaCorriendoUI = logicaPomodoro.estaCorriendo
                tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos
                estadoActualUI = logicaPomodoro.estadoActual
                sesionesCompletadasUI = logicaPomodoro.sesionesCompletadas

                // ===== NUEVO: Ocultar mensaje de tiempo terminado =====
                tiempoTerminadoEventoUI = false
            }
        ) {
            Text("Reiniciar todo (contador a 0)")
        }

        // ===== BOTONES PARA CAMBIAR MANUALMENTE DE MODO =====
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    logicaPomodoro.cambiarAModoTrabajo()
                    estaCorriendoUI = logicaPomodoro.estaCorriendo
                    tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos
                    estadoActualUI = logicaPomodoro.estadoActual
                    // Opcional: si quieres que al cambiar manualmente también se oculte el mensaje de tiempo terminado
                    tiempoTerminadoEventoUI = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text("🍅 Trabajo", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    logicaPomodoro.cambiarAModoDescansoCorto()
                    estaCorriendoUI = logicaPomodoro.estaCorriendo
                    tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos
                    estadoActualUI = logicaPomodoro.estadoActual
                    tiempoTerminadoEventoUI = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("☕ Descanso corto", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    logicaPomodoro.cambiarAModoDescansoLargo()
                    estaCorriendoUI = logicaPomodoro.estaCorriendo
                    tiempoRestanteUI = logicaPomodoro.tiempoRestanteSegundos
                    estadoActualUI = logicaPomodoro.estadoActual
                    tiempoTerminadoEventoUI = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text("🌿 Descanso largo", fontSize = 14.sp)
            }
        }

        // ===== NUEVO: Botón para probar el sonido manualmente =====
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Reproducir sonido de prueba
                try {
                    if (reproductorSonido != null) {
                        if (reproductorSonido.isPlaying) {
                            reproductorSonido.seekTo(0)
                        } else {
                            reproductorSonido.start()
                        }
                        println("DEBUG: Sonido de prueba reproducido")
                    } else {
                        println("ADVERTENCIA: Reproductor no disponible para prueba.")
                    }
                } catch (excepcion: Exception) {
                    println("ERROR en sonido de prueba: ${excepcion.message}")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("🔊 Probar sonido")
        }
    }
}