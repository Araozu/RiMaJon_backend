package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuDobleSecuencia(): Boolean {
    val cartas = arrayListOf(
        10, 11, 12, 12, 14, 15, 52, 52, 192, 192
    )
    val op = OportunidadRon.verificar(
        valorCarta = 52,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.DobleSecuencia)
}

fun testYakuRealeza(): Boolean {
    val cartas = arrayListOf(
        6, 8, 10, 40, 42, 44, 96, 96, 256, 256
    )
    val op = OportunidadRon.verificar(
        valorCarta = 256,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.Realeza)
}

fun testYakuTripleTriples(): Boolean {
    val cartas = arrayListOf(
        2,
        2,
        3,
        20,
        20,
        96,
        96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas,
        gruposAbiertos = arrayListOf(
            arrayListOf(34, 34, 35)
        )
    )
    return existeYaku(op, Yaku.TripleTriples)
}

fun testYakuInterior(): Boolean {
    val cartas = arrayListOf(
        4, 6, 8, 6, 8, 10, 48
    )
    val op = OportunidadRon.verificar(
        valorCarta = 48,
        cartasMano = cartas,
        gruposAbiertos = arrayListOf(
            arrayListOf(44, 44, 45)
        )
    )
    return existeYaku(op, Yaku.Interior)
}
