package dev.araozu.juego

data class Mano(
    val cartas: ArrayList<Int>,
    val cartasReveladas: ArrayList<ArrayList<Int>> = arrayListOf(),
    val descartes: ArrayList<Int> = arrayListOf(),
    var sigCarta: Int = -1,
    var oportunidades: ArrayList<Oportunidad> = arrayListOf()
) {

    fun obtenerManoPrivada(): Mano {
        val l = ArrayList<Int>()
        l.addAll(cartas.map { 0 })
        return this.copy(
            cartas = l,
            sigCarta = if (sigCarta != -1) 0 else sigCarta
        )
    }

}
