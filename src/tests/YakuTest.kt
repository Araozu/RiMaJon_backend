package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun c(c: Carta): Int = c.valor
fun carr(vararg carr: Carta): ArrayList<Int> {
    val cartas = arrayListOf<Int>()
    cartas.addAll(carr.map(::c))
    return cartas
}

fun existeYaku(op: OportunidadRon?, yaku: Yaku): Boolean {
    if (op == null) return false
    return null != op.yaku.find { it == yaku }
}

fun impErr(res: Boolean, nombreYaku: String) {
    if (!res) System.err.println("Error al ejecutar yaku $nombreYaku")
}

fun main() {
    // Yaku10
    impErr(testYakuVerde(), "Verde")
    impErr(testYakuDragonesFull(), "Dragones Full")

    // Yaku7
    impErr(testYakuA10(), "A-10")
    impErr(testYakuEscaleraPerfecta(), "Escalera Perfecta")
    impErr(testYakuRealezaFull(), "Realeza Full")
    impErr(testYakuRealezaDragones(), "Realeza y Dragones")

    // Yaku5
    impErr(testYakuEscaleraFull(), "Escalera Full")

}