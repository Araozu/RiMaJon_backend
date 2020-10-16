package dev.araozu.juego

data class OportunidadTri(override val cartaDescartada: Int, val cartas: Pair<Int, Int>) :
    Oportunidad {

    override val nombreOportunidad: String = "Tri"

    companion object {

        private fun arrlCartasContieneTri(carta: Int, arrl: ArrayList<Int>): Pair<Int, Int>? {
            var numCartasEncontradas = 0

            val datos = Array(2) { 0 }
            // Elimina el último bit para que no se distinga entre cartas de corazon/trebol/etc
            val valorCarta = (carta ushr 1) shl 1
            for (c in arrl) {
                val valorCartaN = (c ushr 1) shl 1
                if (valorCarta == valorCartaN) {
                    datos[numCartasEncontradas] = c
                    numCartasEncontradas++
                }
                if (numCartasEncontradas == 2) return Pair(datos[0], datos[1])
            }
            return null
        }

        fun verificar(valorCarta: Int, cartasMano: ArrayList<Int>): OportunidadTri? {
            val r = arrlCartasContieneTri(valorCarta, cartasMano)
            return if (r != null) {
                OportunidadTri(valorCarta, r)
            } else {
                null
            }
        }
    }

}
