package dev.araozu.juego

class GestorDora(private val cartas: ArrayList<Int>) {

    private val doraCerrado = arrayListOf(cartas[0])
    private val doraAbierto = arrayListOf<Int>()
    var turnosSigDora = 32
    var turnosRestantesDoraCerrado = turnosSigDora // 32 16 8 4
        private set

    operator fun component1(): Array<Int> {
        val numDora = doraCerrado.size
        return Array(5) {
            if (it < numDora) doraCerrado[it]
            else 0
        }
    }

    operator fun component2(): Array<Int> {
        val numDora = doraCerrado.size
        return Array(5) {
            if (it < numDora) doraAbierto[it]
            else 0
        }
    }

    fun actualizarDoraCerrado() {
        if (doraCerrado.size >= 5) return
        turnosRestantesDoraCerrado--
        if (turnosRestantesDoraCerrado == 0) {
            doraCerrado.add(cartas[doraCerrado.size])
            turnosSigDora /= 2
            turnosRestantesDoraCerrado = turnosSigDora
        }
        if (doraCerrado.size == 5) {
            turnosRestantesDoraCerrado = -1
        }
    }

    fun actualizarDoraAbierto() {
        if (doraCerrado.size >= 5) return
        doraAbierto.add(cartas[5 + doraAbierto.size])
    }

}
