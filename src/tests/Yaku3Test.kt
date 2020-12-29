package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuExterior(): Boolean {
    val cartas = arrayListOf(
        2,
        2,
        3,
        20,
        20,
        34,
        34,
        35,
        96,
        96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.Exterior)
}

fun testYakuTripleTriplesCerrados(): Boolean {
    val cartas = arrayListOf(
        2,
        2,
        3,
        20,
        20,
        34,
        34,
        35,
        96,
        96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.TripleTriplesCerrados)
}
