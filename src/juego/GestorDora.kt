package dev.araozu.juego

class GestorDora(cartasIniciales: Array<Int>) {

    private val cartas = Array(5) {-1}
    val dora = arrayListOf(cartas[0])
    private var turnosSigDora = 20
    var turnosRestantesDora = turnosSigDora // 20 15 10 5
        private set

    init {
        for (i in 0 until 5) {
            cartas[i] = cartasIniciales[i]
        }
    }

    operator fun component1(): ArrayList<Int> {
        return dora
    }

    fun actualizarDora() {
        if (dora.size >= 5) return
        turnosRestantesDora--

        if (turnosRestantesDora == 0) {
            dora.add(cartas[dora.size])
            turnosSigDora -= 5
            turnosRestantesDora = turnosSigDora
        }

        if (dora.size == 5) {
            turnosRestantesDora = -1
        }
    }

}
