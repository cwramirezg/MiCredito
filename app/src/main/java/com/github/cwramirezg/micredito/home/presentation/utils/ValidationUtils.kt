package com.github.cwramirezg.micredito.home.presentation.utils

object ValidationUtils {

    fun validarMonto(monto: Double, minimo: Double, maximo: Double): String? {
        return when {
            monto <= 0 -> "El monto debe ser mayor a cero"
            monto < minimo -> "El monto mínimo es ${minimo}"
            monto > maximo -> "El monto máximo es ${maximo}"
            else -> null
        }
    }

    fun validarPlazo(plazo: Int, minimo: Int, maximo: Int): String? {
        return when {
            plazo <= 0 -> "El plazo debe ser mayor a cero"
            plazo < minimo -> "El plazo mínimo es $minimo meses"
            plazo > maximo -> "El plazo máximo es $maximo meses"
            else -> null
        }
    }

    fun calcularRiesgoCredito(monto: Double, ingresos: Double): String {
        val ratio = monto / ingresos
        return when {
            ratio <= 0.3 -> "Riesgo bajo"
            ratio <= 0.5 -> "Riesgo medio"
            else -> "Riesgo alto"
        }
    }
}
