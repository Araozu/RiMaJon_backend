package dev.araozu.juego

class OportunidadWin(override val cartaDescartada: Int) : Oportunidad {

    override val nombreOportunidad = "Win"

    companion object {

        private fun <T : Carta> ArrayList<out T>.cartaExisteEnArrl(valorCarta: Int): Boolean {
            for (c in this) {
                val valorCartaParActual = (c.valor ushr 1) shl 1
                if (valorCartaParActual == valorCarta) return true
            }
            return false
        }

        private fun <T : Carta> ArrayList<out T>.eliminarCarta(valorCarta: Int): T {
            for (c in this) {
                val valorCartaParActual = (c.valor ushr 1) shl 1
                if (valorCartaParActual == valorCarta) {
                    this.remove(c)
                    return c
                }
            }

            throw Error("El valor de la carta no existe en el array.")
        }

        private fun obtTri(cartas: ArrayList<Carta>): ArrayList<ArrayList<Carta>> {
            val arrlR = arrayListOf<ArrayList<Carta>>()
            var i = 0
            while (i + 2 < cartas.size) {
                val primeraCarta = cartas[i]
                val valorPrimeraCartaPar = (primeraCarta.valor ushr 1) shl 1
                val primerElem = (primeraCarta.valor ushr 1) shl 1
                val segundoElem = (cartas[i + 1].valor ushr 1) shl 1
                val tercerElem = (cartas[i + 2].valor ushr 1) shl 1
                if (primerElem == segundoElem && primerElem == tercerElem) {
                    val c1 = cartas.eliminarCarta(valorPrimeraCartaPar)
                    val c2 = cartas.eliminarCarta(valorPrimeraCartaPar)
                    val c3 = cartas.eliminarCarta(valorPrimeraCartaPar)
                    arrlR.add(arrayListOf(c1, c2, c3))
                } else {
                    i++
                }
            }
            return arrlR
        }

        private fun obtPar(cartas: ArrayList<Carta>): ArrayList<ArrayList<Carta>> {
            val arrlR = arrayListOf<ArrayList<Carta>>()
            var i = 0
            while (i + 2 < cartas.size) {
                val primeraCarta = cartas[i]
                val valorPrimeraCartaPar = (primeraCarta.valor ushr 1) shl 1
                val primerElem = (primeraCarta.valor ushr 1) shl 1
                val segundoElem = (cartas[i + 1].valor ushr 1) shl 1
                if (primerElem == segundoElem) {
                    val c1 = cartas.eliminarCarta(valorPrimeraCartaPar)
                    val c2 = cartas.eliminarCarta(valorPrimeraCartaPar)
                    arrlR.add(arrayListOf(c1, c2))
                } else {
                    i++
                }
            }
            return arrlR
        }

        private fun obtSeq1(cartas: ArrayList<CartaNumero>): ArrayList<ArrayList<CartaNumero>> {
            val arrlCartas = arrayListOf<ArrayList<CartaNumero>>()
            var i = 0
            while (i < cartas.size) {
                val primeraCarta = cartas[i]
                val valorCartaPar = (primeraCarta.valor ushr 1) shl 1

                if (
                    cartas.cartaExisteEnArrl(valorCartaPar + 1)
                    && cartas.cartaExisteEnArrl(valorCartaPar + 2)
                ) {
                    val c1 = cartas.eliminarCarta(valorCartaPar)
                    val c2 = cartas.eliminarCarta(valorCartaPar + 1)
                    val c3 = cartas.eliminarCarta(valorCartaPar + 2)
                    arrlCartas.add(arrayListOf(c1, c2, c3))
                } else {
                    i++
                }
            }

            return arrlCartas
        }

        private fun obtSeq2(cartas: ArrayList<CartaNumero>): ArrayList<ArrayList<CartaNumero>> {
            val arrlCartas = arrayListOf<ArrayList<CartaNumero>>()
            var i = cartas.size - 1
            while (i < cartas.size) {
                val primeraCarta = cartas[i]
                val valorCartaPar = (primeraCarta.valor ushr 1) shl 1

                if (
                    cartas.cartaExisteEnArrl(valorCartaPar - 1)
                    && cartas.cartaExisteEnArrl(valorCartaPar - 2)
                ) {
                    val c1 = cartas.eliminarCarta(valorCartaPar)
                    val c2 = cartas.eliminarCarta(valorCartaPar - 1)
                    val c3 = cartas.eliminarCarta(valorCartaPar - 2)
                    arrlCartas.add(arrayListOf(c1, c2, c3))
                    i -= 3
                } else {
                    i--
                }
            }

            return arrlCartas
        }

        private fun obtenerPosiblesSeq(cartas: ArrayList<CartaNumero>): Pair<ArrayList<ArrayList<CartaNumero>>, ArrayList<ArrayList<CartaNumero>>> {
            val cartas2 = ArrayList<CartaNumero>(cartas.size)
            cartas2.addAll(cartas)

            val seqOrden1 = obtSeq1(cartas)
            val seqOrden2 = obtSeq2(cartas)

            return Pair(seqOrden1, seqOrden2)
        }

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