package com.felipe.pomodoroapp.utils

fun formateoTiempo(seconds: Int): String {
    val minutos = seconds / 60
    val segundos = seconds % 60
    return String.format("%02d:%02d", minutos, segundos)
}