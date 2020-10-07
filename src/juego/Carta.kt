package dev.araozu.juego

sealed class Carta(val valor: Int) {
    companion object {
        fun obtenerCartaEspecifica(valor: Int): Carta {
            return when (valor) {
                in 2..21 -> CartaNumeroNegro(valor)
                in 34..53 -> CartaNumeroRojo(valor)
                64 -> CartaDragonNegro()
                96 -> CartaDragonRojo()
                128 -> CartaDragonVerde()
                160 -> CartaDragonAzul()
                192 -> CartaRealezaJ()
                224 -> CartaRealezaQ()
                256 -> CartaRealezaK()
                else -> {
                    throw IllegalArgumentException("Valor de carta ilegal. $valor")
                }
            }
        }

    }
}

sealed class CartaNumero(valor: Int, val numero: Int = (valor shl 27) ushr 28) : Carta(valor) {

    companion object {
        fun filtrarCartasRojo(valores: ArrayList<Int>): ArrayList<CartaNumeroRojo> {
            val arrl = arrayListOf<CartaNumeroRojo>()

            valores.forEach {
                val carta = obtenerCartaEspecifica(it)
                if (carta is CartaNumeroRojo) arrl.add(carta)
            }

            return arrl
        }

        fun filtrarCartasNegro(valores: ArrayList<Int>): ArrayList<CartaNumeroNegro> {
            val arrl = arrayListOf<CartaNumeroNegro>()

            valores.forEach {
                val carta = obtenerCartaEspecifica(it)
                if (carta is CartaNumeroNegro) arrl.add(carta)
            }

            return arrl
        }
    }

}

class CartaNumeroNegro(valor: Int) : CartaNumero(valor)
class CartaNumeroRojo(valor: Int) : CartaNumero(valor)

sealed class CartaDragon(valor: Int) : Carta(valor)

class CartaDragonNegro : CartaDragon(64)
class CartaDragonRojo : CartaDragon(96)
class CartaDragonVerde : CartaDragon(128)
class CartaDragonAzul : CartaDragon(160)

sealed class CartaRealeza(valor: Int) : Carta(valor)

class CartaRealezaJ : CartaRealeza(192)
class CartaRealezaQ : CartaRealeza(224)
class CartaRealezaK : CartaRealeza(256)
