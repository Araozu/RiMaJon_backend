package dev.araozu.juego

data class Mano(
    var cartas: ArrayList<Int> = arrayListOf(),
    val cartasReveladas: ArrayList<ArrayList<Int>> = arrayListOf(),
    val descartes: ArrayList<Int> = arrayListOf(),
    var sigCarta: Int = -1,
    var oportunidades: ArrayList<Oportunidad> = arrayListOf(),
    var dragon: Dragon = Dragon.Negro,
    var esGanador: Boolean = false
) {

    fun obtenerManoPrivada(): Mano {
        if (esGanador) return this

        val l = ArrayList<Int>()
        l.addAll(cartas.map { 0 })
        return this.copy(
            cartas = l,
            sigCarta = if (sigCarta != -1) 0 else sigCarta,
            oportunidades = arrayListOf()
        )
    }

    private fun validarHay10Cartas(): Boolean =
        cartas.size + (cartasReveladas.size * 3) + (if (sigCarta != -1) 1 else 0) - 1 == 10

    /**
     * Intenta descartar una carta de la mano y devuelve si fue correcto
     * @param cartaDescartada La carta a remover de la mano
     * @return true si se descarto la carta, false sino
     */
    fun descartarCarta(cartaDescartada: Int): Boolean {
        if (!validarHay10Cartas()) {
            System.err.println("Error al descartar carta: Hacerlo dejaria al jugador con menos de 10 cartas")
            return false
        }

        if (sigCarta == cartaDescartada) {
            sigCarta = -1
        } else {
            val posCarta = cartas.indexOf(cartaDescartada)
            if (posCarta != -1) {
                cartas.removeAt(posCarta)

                // Incluir la carta entrante a la mano del jugador
                if (sigCarta != -1) {
                    cartas.add(sigCarta)
                    sigCarta = -1
                }
            } else {
                System.err.println("Error al descartar carta: El jugador no tiene dicha carta.")
                return false
            }
        }

        descartes.add(cartaDescartada)
        return true
    }

    // TODO: Cachear la mano privada y actualizarla solo cuando se llama tri/seq

}
