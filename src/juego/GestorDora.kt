package dev.araozu.juego

class GestorDora(cartasIniciales: Array<Int>) {

    private val cartas = Array(5) { -1 }
    val dora: ArrayList<Int>
    private var turnosSigDora = 31 // 31 15 7 3
    var turnosRestantesDora = turnosSigDora
        private set

    init {
        for (i in 0 until 5) {
            cartas[i] = cartasIniciales[i]
        }
        dora = arrayListOf(cartas[0])
    }

    operator fun component1(): ArrayList<Int> {
        return dora
    }

    fun actualizarDora() {
        if (dora.size >= 5) return
        turnosRestantesDora--

        if (turnosRestantesDora == 0) {
            dora.add(cartas[dora.size])
            turnosSigDora = (turnosSigDora - 1) / 2
            turnosRestantesDora = turnosSigDora
        }

        if (dora.size == 5) {
            turnosRestantesDora = -1
        }
    }

}
