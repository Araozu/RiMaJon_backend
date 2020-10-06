package dev.araozu.juego

data class OportunidadSeq(override val cartaDescartada: Int, val combinaciones: ArrayList<Pair<Int, Int>>) :
    Oportunidad {

    override val nombreOportunidad: String = "Seq"

    companion object {

        private fun arrlCartasContiene(arrl: ArrayList<CartaNumero>, v1: Int, v2: Int): Pair<Int, Int>? {
            var v1E: Int? = null
            var v2E: Int? = null
            for (c in arrl) {
                if (c.numero == v1) v1E = c.numero
                if (c.numero == v2) v2E = c.numero
                if (v1E != null && v2E != null) return Pair(v1E, v2E)
            }
            return null
        }

        fun verificar(valorCarta: Int, cartasMano: ArrayList<Int>): OportunidadSeq? {
            return when (val carta = Carta.obtenerCartaEspecifica(valorCarta)) {
                is CartaNumero -> {
                    val valor = carta.numero

                    val cartasFiltradas = Carta.filtrarCartas<CartaNumero>(cartasMano, carta::class)

                    val arrlRetorno = arrayListOf<Pair<Int, Int>>()

                    val seq1 = arrlCartasContiene(cartasFiltradas, valor + 1, valor + 2)
                    if (seq1 != null)  arrlRetorno.add(seq1)
                    val seq2 = arrlCartasContiene(cartasFiltradas, valor - 1, valor + 1)
                    if (seq2 != null)  arrlRetorno.add(seq2)
                    val seq3 = arrlCartasContiene(cartasFiltradas, valor - 1, valor - 2)
                    if (seq3 != null)  arrlRetorno.add(seq3)

                    return if (arrlRetorno.isNotEmpty()) OportunidadSeq(valorCarta, arrlRetorno) else null
                }
                else -> null
            }

        }
    }

}
