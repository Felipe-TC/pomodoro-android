package com.felipe.pomodoroapp

// ===== ENUM (lo mismo)
enum class EstadoPomodoro {
    TRABAJO,            // Trabajo (25 min)
    DESCANSO_CORTO,     // Descanso corto (5 min)
    DESCANSO_LARGO,     // Descanso largo (15 min)
}

// ===== CLASE CON PURA LOGICA =====
class LogicaPomodoro {
    // ===== CONSTANTES (no cambian) =====
    val minutosTrabajo: Int = 25
    val minutosDescansoCorto: Int = 5
    val minutosDescansoLargo: Int = 15

    // ===== VARIABLES DE LOGICA (cambian, pero son NORMALES) =====
    var estadoActual: EstadoPomodoro = EstadoPomodoro.TRABAJO
    var tiempoRestanteSegundos: Int = minutosTrabajo * 60
    var estaCorriendo: Boolean = false
    var sesionesCompletadas: Int = 0
    var contadorSesionesTrabajo: Int = 0

    // ===== CONSTANTES DE CONFIGURACION =====
    val sesionesAntesDescansoLargo: Int = 4

    // METODOS PUBLICOS
}