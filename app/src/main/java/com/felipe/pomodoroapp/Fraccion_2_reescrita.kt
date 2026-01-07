// Fraccion_2_reescrita.kt - Version mejorada (mi estilo)
package com.felipe.pomodoroapp

class Fraccion_2_reescrita(x: Int, y: Int) {    // Constructor va entre parentesis después del nombre
    // Atributos publicos (val = constante, no cambia)
    val numerador: Int = x      // val = constante (no cambia)
    val denominador: Int = y

    // Bloque init: se ejcuta al CREAR el objeto
    // Similar a __init__ en Python pero separado
    init {
        if (denominador == 0) {
            throw IllegalArgumentException("denominador cero")
        }
    }

    // Metodo para sumar
    fun suma(otra: Fraccion_2_reescrita): Fraccion_2_reescrita {
        val nuevoNumerador = this.numerador * otra.denominador + this.denominador * otra.numerador
        val nuevoDenominador = this.denominador * otra.denominador

        return Fraccion_2_reescrita(nuevoNumerador, nuevoDenominador)
    }

    // Metido para comparar (mayor que)
    fun mayorQue(otra: Fraccion_2_reescrita): Boolean {
        // Comparacion: a/b > c/d => a*d > b*c
        val izquierda = this.numerador * otra.denominador
        val derecha = this.denominador * otra.numerador

        return izquierda > derecha
    }

    // Metodo para convertir a String
    override fun toString(): String {
        return "$numerador/$denominador"
    }

    //Metodo estatico para crear desde String (extra)
    companion object {
        fun desdeString(str: String): Fraccion_2_reescrita {
            val partes = str.split("/")
            return Fraccion_2_reescrita(partes[0].toInt(), partes[1].toInt())
        }
    }
}