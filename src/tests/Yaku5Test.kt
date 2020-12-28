package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuEscaleraFull(): Boolean {
    val cartas = arrayListOf(
        2,
        3,
        4,
        6,
        8,
        10,
        13,
        14,
        16,
        18
    )
    val op = OportunidadRon.verificar(
        valorCarta = 20,
        cartasMano = cartas
    )
    return existeYaku(op, Yaku.EscaleraFull)
}
