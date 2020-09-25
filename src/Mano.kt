package dev.araozu

data class Mano(
    val cartas: ArrayList<Int>,
    val allIn: Boolean = false,
    val cartasReveladas: ArrayList<ArrayList<Int>> = ArrayList(),
    val descartes: ArrayList<Int> = ArrayList(),
    var sigCarta: Int = -1
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
