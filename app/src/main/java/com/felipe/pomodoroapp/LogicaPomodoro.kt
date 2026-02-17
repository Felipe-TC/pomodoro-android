package com.felipe.pomodoroapp

// ===== ENUM (lo mismo) =====
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

    // ===== NUEVO: VARIABLE para manejar evento de tiempo terminado =====
    // Esta variable actúa como una "bandera" o "señal"
    var tiempoTerminadoEvento: Boolean = false
        private set     // Solo se puede modificar desde dentro de esta clase

    // METODOS PUBLICOS (interfaz de la clase) =====

    // Metodo: Alternar entre iniciar y pausar
    fun alternarTemporizador() {
        estaCorriendo = !estaCorriendo
    }

    // Metodo: Reiniciar el tiempo actual (sin cambiar estado)
    fun reiniciarTemporizadorActual() {
        estaCorriendo = false
        tiempoRestanteSegundos = obtenerTiempoParaEstado(estadoActual)
    }

    // Metodo: Cambiar a modo trabajo
    fun cambiarAModoTrabajo() {
        estadoActual = EstadoPomodoro.TRABAJO
        reiniciarTemporizadorActual()
    }

    // Metodo: Cambiar a modo descanso corto
    fun cambiarAModoDescansoCorto() {
        estadoActual = EstadoPomodoro.DESCANSO_CORTO
        reiniciarTemporizadorActual()
    }

    // Metodo: Cambiar a modo descanso largo
    fun cambiarAModoDescansoLargo() {
        estadoActual = EstadoPomodoro.DESCANSO_LARGO
        reiniciarTemporizadorActual()
    }

    // Metodo: Reiniciar TODO (incluyendo contadores)
    fun reiniciarTodo() {
        estaCorriendo   = false
        estadoActual    = EstadoPomodoro.TRABAJO
        sesionesCompletadas = 0
        contadorSesionesTrabajo = 0
        tiempoRestanteSegundos = obtenerTiempoParaEstado(estadoActual)
    }

    // Metodo: Actualizar el tiempo (llamar cada segundo)
    fun actualizarTiempo() {
        if (estaCorriendo && tiempoRestanteSegundos > 0) {
            // Reducir un segundo
            tiempoRestanteSegundos = tiempoRestanteSegundos - 1

            // Si llego a cero, manejar el fin
            if (tiempoRestanteSegundos == 0) {
                manejarFinDeTiempo()
            }
        }
    }

    // ===== NUEVO: METODO para resetear el evento =====
    // Se llama despues de que la UI ha respondido al evento
    fun resetearEventoTiempoTerminado() {
        tiempoTerminadoEvento = false
    }


    // METODOS PRIVADOS (implementacion interna) =====

    // Metodo privado: Obtener tiempo en segundos para un estado
    private fun obtenerTiempoParaEstado(estado: EstadoPomodoro): Int {
        return when (estado) {
            EstadoPomodoro.TRABAJO -> minutosTrabajo * 60
            EstadoPomodoro.DESCANSO_CORTO -> minutosDescansoCorto * 60
            EstadoPomodoro.DESCANSO_LARGO -> minutosDescansoLargo * 60
        }
    }

    // ===== MODIFICAR el metodo manejarFinDeTiempo() =====
    // Cambia SOLO este metodo, los demas se mantienen igual
    // Metodo privado: Manejar cuando el tiempo llega a cero
    private fun manejarFinDeTiempo() {
        // 1. Detener el temporizador
        estaCorriendo = false

        // 2. NUEVO: ACTIVAR EL EVENTO para notificar a la UI
        tiempoTerminadoEvento = true

        // 3. Ejecutar logica segun el estado actual
        when (estadoActual) {
            EstadoPomodoro.TRABAJO -> manejarFinTrabajo()
            EstadoPomodoro.DESCANSO_CORTO -> manejarFinDescansoCorto()
            EstadoPomodoro.DESCANSO_LARGO -> manejarFinDescansoLargo()
        }

        // 4. Mensaje de depuracion actualizado
        println("DEBUG Tiempo terminado. Estado: $estadoActual - EVENTO ACTIVADO")
    }

    // Metodo privado: Manejar fin de tiempo de trabajo
    private fun manejarFinTrabajo() {
        // Aumentar contadores
        sesionesCompletadas = sesionesCompletadas + 1
        contadorSesionesTrabajo = contadorSesionesTrabajo + 1

        // Decidir que tipo de descanso toca
        val tocaDescansoLargo = contadorSesionesTrabajo >= sesionesAntesDescansoLargo

        if (tocaDescansoLargo) {
            estadoActual = EstadoPomodoro.DESCANSO_LARGO
            contadorSesionesTrabajo = 0 // Reiniciar contador
        } else {
            estadoActual = EstadoPomodoro.DESCANSO_CORTO
        }

        // Actualizar tiempo para el nuevo estado
        tiempoRestanteSegundos = obtenerTiempoParaEstado(estadoActual)
    }

    // Metodo privado: Manejar fin de descanso corto
    private fun manejarFinDescansoCorto() {
        estadoActual = EstadoPomodoro.TRABAJO
        tiempoRestanteSegundos = obtenerTiempoParaEstado(estadoActual)
    }

    // Metodo privado: Manejar fin de descanso largo
    private fun manejarFinDescansoLargo() {
        estadoActual = EstadoPomodoro.TRABAJO
        tiempoRestanteSegundos = obtenerTiempoParaEstado(estadoActual)
    }
}