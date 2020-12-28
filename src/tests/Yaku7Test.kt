package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuA10(): Boolean {
    val cartasMano = arrayListOf(
        2,
        2,
        3,
        20,
        20,
        21,
        34,
        34,
        52,
        52
    )
    val op = OportunidadRon.verificar(
        valorCarta = 35,
        cartasMano = cartasMano
    )
    return existeYaku(op, Yaku.A10)
}

fun testYakuEscaleraPerfecta(): Boolean {
    val cartas = arrayListOf(
        2,
        2,
        4,
        6,
        8,
        10,
        12,
        14,
        16,
        18
    )
    val op = OportunidadRon.verificar(
        valorCarta = 20,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.EscaleraPerfecta)
}

fun testYakuRealezaFull(): Boolean {
    val cartas = arrayListOf(
        2,
        192,
        192,
        192,
        224,
        224,
        224,
        256,
        256,
        256
    )
    val op = OportunidadRon.verificar(
        valorCarta = 2,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.RealezaFull)
}

fun testYakuRealezaDragones(): Boolean {
    val cartas = arrayListOf(
        256,
        64,
        64,
        64,
        128,
        128,
        128,
        192,
        192,
        192
    )
    val op = OportunidadRon.verificar(
        valorCarta = 256,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.RealezaDragones)
}
