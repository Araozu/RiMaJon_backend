package dev.araozu

class Mano(
    val cartas: List<Int>,
    val allIn: Boolean = false,
    val cartaSig: Int? = null,
    val cartasReveladas: ArrayList<ArrayList<Int>> = ArrayList()
) {

    fun obtenerManoPrivada(): Mano {
        val l = cartas.map { 0 }
        return Mano(l, allIn, cartaSig, cartasReveladas)
    }

}
