package com.felipe.pomodoroapp

class Fraccion(x: Int, y: Int) {    // Constructor va entre parentesis después del nombre
    val a: Int = x      // val = constante (no cambia)
    val b: Int = y

    init {          // Bloque init = codigo que se ejecuta al crear el objeto
        if (b == 0) {
            throw IllegalArgumentException("denominador cero")
        }
    }

    // Metodo para sumar
    fun suma(otra: Fraccion): Fraccion {
        return Fraccion(
            this.a * otra.b + this.b * otra.a,
            this.b * otra.b
        )
    }

    // Metido para comparar (como en el PDF)
    fun mayorQue(otra: Fraccion): Boolean {
        return this.a * otra.b > this.b * otra.a
    }

    // Metodo para convertir a String
    override fun toString(): String {
        return "$a/$b"
    }

    //Metodo estatico para crear desde String (extra)
    companion object {
        fun desdeString(str: String): Fraccion {
            val partes = str.split("/")
            return Fraccion(partes[0].toInt(), partes[1].toInt())
        }
    }
}