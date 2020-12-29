package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuDobleSecuenciaPura(): Boolean {
    val cartas = arrayListOf(
        2,
        2,
        4,
        4,
        6,
        6,
        34,
        34,
        35,
        96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.DobleSecuenciaPura)
}

fun testYakuSemiExterior(): Boolean {
    val cartas = arrayListOf(
        2,
        4,
        6,
        20,
        20,
        34,
        36,
        38,
        96,
        96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.SemiExterior)
}

fun testYakuRojo(): Boolean {
    val cartas = arrayListOf(
        38,
        38,
        38,
        52,
        52,
        34,
        34,
        35,
        c(CartaDragonRojo()),
        c(CartaDragonRojo())
    )
    val op = OportunidadRon.verificar(
        valorCarta = c(CartaDragonRojo()),
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.Rojo)
}

fun testYakuNegro(): Boolean {
    val cartas = arrayListOf(
        2, 2, 2, 10, 10, 10, 16, 16, 20, 20
    )
    val op = OportunidadRon.verificar(
        valorCarta = 20,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.Negro)
}

fun testYakuEscalera(): Boolean {
    val cartas = arrayListOf(
        2, 4, 6, 8, 10, 12, 14, 16, 18, 96
    )
    val op = OportunidadRon.verificar(
        valorCarta = 96,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.Escalera)
}
