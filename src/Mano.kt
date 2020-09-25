package dev.araozu

data class Mano(
    val cartas: List<Int>,
    val allIn: Boolean = false,
    val cartasReveladas: ArrayList<ArrayList<Int>> = ArrayList(),
    val descartes: List<Int> = ArrayList(),
    val sigCarta: Int = -1
) {

    fun obtenerManoPrivada(): Mano {
        val l = cartas.map { 0 }
        return this.copy(
            cartas = l,
            sigCarta = if (sigCarta != -1) 0 else sigCarta
        )
    }

}
