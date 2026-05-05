package com.felipe.pomodoroapp.model

data class Configuracion(
    val minutosTrabajo: Int = 25,
    val minutosDescansoCorto: Int = 5,
    val minutosDescansoLargo: Int = 15
)