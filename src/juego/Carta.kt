package dev.araozu.juego

import kotlin.reflect.KClass

sealed class Carta(private val valor: Int) {
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
                    throw IllegalArgumentException("Valor de carta ilegal.")
                }
            }
        }

        /**
         * Filtra un array list de cartas segun su tipo de clase. El parámetro T debe ser la misma clase
         * que el parametro clase, o su clase padre.
         * @param valores El ArrayList del cual se filtrará
         * @param clase La clase que determina el filtro
         */
        fun <T: Carta> filtrarCartas(valores: ArrayList<Int>, clase: KClass<out Carta>): ArrayList<T> {
            val arrl = arrayListOf<T>()

            valores.forEach {
                val carta = obtenerCartaEspecifica(it)
                if (carta::class == clase) arrl.add(carta as T)
            }

            return arrl
        }
    }
}

sealed class CartaNumero(valor: Int, val numero: Int = (valor shl 27) ushr 28) : Carta(valor)

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
