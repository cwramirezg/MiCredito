package com.github.cwramirezg.micredito.home.domain.entities

data class SimulacionCredito(
    val monto: Double,
    val plazo: Int, // En meses
    val tasaInteres: Double, // Tasa anual
    val cuotaMensual: Double,
    val interesTotal: Double,
    val montoTotal: Double,
    val fechaSimulacion: Long = System.currentTimeMillis()
) {
    companion object {
        fun calcular(
            monto: Double,
            plazo: Int,
            tasaAnual: Double
        ): SimulacionCredito {
            // Cálculo de interés simple: I = P * r * t
            val interesTotal = monto * (tasaAnual / 100) * (plazo / 12.0)
            val montoTotal = monto + interesTotal
            val cuotaMensual = montoTotal / plazo

            return SimulacionCredito(
                monto = monto,
                plazo = plazo,
                tasaInteres = tasaAnual,
                cuotaMensual = cuotaMensual,
                interesTotal = interesTotal,
                montoTotal = montoTotal
            )
        }
    }

    fun esValida(): Boolean = monto > 0 && plazo > 0 && tasaInteres >= 0
}
