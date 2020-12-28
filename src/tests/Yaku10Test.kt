package dev.araozu.tests

import dev.araozu.juego.*
import dev.araozu.juego.yaku.Yaku

fun testYakuVerde(): Boolean {
    val cartasMano = carr(
        CartaDragonVerde(),
        CartaDragonVerde(),
        CartaRealezaJ(),
        CartaRealezaJ(),
        CartaRealezaK(),
        CartaRealezaK(),
        CartaRealezaK(),
        CartaRealezaQ(),
        CartaRealezaQ(),
        CartaRealezaQ()
    )
    val op = OportunidadRon.verificar(
        valorCarta = c(CartaDragonVerde()),
        cartasMano = cartasMano
    )

    return existeYaku(op, Yaku.Verde)
}

fun testYakuDragonesFull(): Boolean {
    val cartas = carr(
        CartaDragonVerde(),
        CartaDragonAzul(),
        CartaDragonAzul(),
        CartaDragonAzul(),
        CartaDragonRojo(),
        CartaDragonRojo(),
        CartaDragonRojo(),
        CartaDragonNegro(),
        CartaDragonNegro(),
        CartaDragonNegro()
    )
    val op = OportunidadRon.verificar(
        valorCarta = c(CartaDragonVerde()),
        cartasMano = cartas
    )

    return existeYaku(op, Yaku.DragonesFull)
}
