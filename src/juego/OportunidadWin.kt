package dev.araozu.juego

class OportunidadWin(override val cartaDescartada: Int) : Oportunidad {

    override val nombreOportunidad = "Win"

    companion object {

        private fun obtSeq1(arrl: ArrayList<CartaNumero>): ArrayList<ArrayList<CartaNumero>> {
            TODO()
        }

        // TODO
        fun verificar(valorCarta: Int, cartasMano: ArrayList<Int>, numGruposAbiertos: Int = 0) {
            val narrl = arrayListOf<Int>()
            narrl.addAll(cartasMano)
            narrl.add(valorCarta)

            val (cartasRojo, cartasRestantes1) = CartaNumero.separarCartasRojo(narrl)
            val (cartasNegro, cartasRestantes2) = CartaNumero.separarCartasNegro(cartasRestantes1)
            val restoCartas = cartasRestantes2.map { Carta.obtenerCartaEspecifica(it) }


        }

    }

}