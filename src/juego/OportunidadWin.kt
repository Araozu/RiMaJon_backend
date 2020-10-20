package dev.araozu.juego

class OportunidadWin(override val cartaDescartada: Int) : Oportunidad {

    override val nombreOportunidad = "Win"

    companion object {

        private fun ArrayList<out CartaNumero>.cartaExisteEnArrl(valorCarta: Int): Boolean {
            for (c in this) {
                if (valorCarta == c.numero) return true
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

        private fun ArrayList<out CartaNumero>.eliminarCartaNumero(valorCarta: Int): CartaNumero {
            for (c in this) {
                if (c.numero == valorCarta) {
                    this.remove(c)
                    return c
                }
            }

            throw Error("El valor de la carta no existe en el array.")
        }

        private fun obtTri(cartas: ArrayList<out Carta>): ArrayList<ArrayList<Carta>> {
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

        private fun obtPar(cartas: ArrayList<out Carta>): ArrayList<ArrayList<Carta>> {
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

        private fun obtSeq1(cartas: ArrayList<out CartaNumero>): ArrayList<ArrayList<Carta>> {
            val arrlCartas = arrayListOf<ArrayList<Carta>>()
            var i = 0
            while (i < cartas.size) {
                val primeraCarta = cartas[i]
                val valorCartaPar = primeraCarta.numero

                if (
                    cartas.cartaExisteEnArrl(valorCartaPar + 1)
                    && cartas.cartaExisteEnArrl(valorCartaPar + 2)
                ) {
                    val c1 = cartas.eliminarCartaNumero(valorCartaPar)
                    val c2 = cartas.eliminarCartaNumero(valorCartaPar + 1)
                    val c3 = cartas.eliminarCartaNumero(valorCartaPar + 2)
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

        private fun obtenerContenedorCartasNumero(cartas: ArrayList<out CartaNumero>): ContenedorGrupos {
            val cartas2 = ArrayList<CartaNumero>(cartas.size)
            cartas2.addAll(cartas)

            val contenedor1 = ContenedorGrupos()
            contenedor1.agregarSeqs(obtSeq1(cartas))
            contenedor1.agregarTris(obtTri(cartas))
            contenedor1.agregarPares(obtPar(cartas))
            contenedor1.agregarHuerfanos(cartas)

            val contenedor2 = ContenedorGrupos()
            contenedor2.agregarSeqs(obtSeq1(cartas2))
            contenedor2.agregarTris(obtTri(cartas2))
            contenedor2.agregarPares(obtPar(cartas2))
            contenedor2.agregarHuerfanos(cartas2)

            val valorCont1 = contenedor1.heuristic()
            val valorCont2 = contenedor2.heuristic()

            return if (valorCont1 > valorCont2) contenedor1 else contenedor2
        }

        fun verificar(valorCarta: Int, cartasMano: ArrayList<Int>): OportunidadWin? {
            val narrl = ArrayList<Int>(cartasMano.size + 1)
            narrl.addAll(cartasMano)
            narrl.add(valorCarta)

            val (cartasRojo, cartasRestantes1) = CartaNumero.separarCartasRojo(narrl)
            val (cartasNegro, cartasRestantes2) = CartaNumero.separarCartasNegro(cartasRestantes1)
            val restoCartas = arrayListOf<Carta>()
            restoCartas.addAll(cartasRestantes2.map { Carta.obtenerCartaEspecifica(it) })

            val contenedorGrupos = ContenedorGrupos()

            // Obtener tris y pares de las cartas que no son numeros
            contenedorGrupos.agregarTris(obtTri(restoCartas))
            contenedorGrupos.agregarPares(obtPar(restoCartas))

            // Obtener tris, pares y seq de las cartas de numeros
            contenedorGrupos.agregarDesdeContenedor(obtenerContenedorCartasNumero(cartasRojo))
            contenedorGrupos.agregarDesdeContenedor(obtenerContenedorCartasNumero(cartasNegro))

            return if (contenedorGrupos.estaListo()) {
                OportunidadWin(valorCarta)
            } else {
                null
            }
        }

    }

}