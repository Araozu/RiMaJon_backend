package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos


// TODO: Las secuencias pueden no estar en orden
internal fun yakuEscaleraFull(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.seqs.size != 3) return false

    var numeroInicialSemiEscalera = 0
    var colorCarta = ""
    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (c !is CartaNumero) return false

            colorCarta = c.color
            numeroInicialSemiEscalera = when (c.numero) {
                1 -> 2
                10 -> 1
                else -> return false
            }
        }
    }

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (c !is CartaNumero) return false

            if (c.numero != numeroInicialSemiEscalera) return false
            if (c.color != colorCarta) return false

            numeroInicialSemiEscalera += 1
        }
    }

    return true
}
