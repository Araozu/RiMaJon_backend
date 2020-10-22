package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos

// TODO
internal fun yakuDobleSecuenciaPura(contenedorGrupos: ContenedorGrupos): Boolean {

    return false
}

internal fun yakuRealeza(contenedorGrupos: ContenedorGrupos): Int {
    var cantidadRealeza = 0
    for (carrl in contenedorGrupos.tris) {
        val cartaTri = carrl[0]
        if (cartaTri.esRey()) cantidadRealeza++
    }

    return cantidadRealeza
}

internal fun yakuTripleSecuenciaCerrada(contenedorGrupos: ContenedorGrupos) =
    contenedorGrupos.tris.size == 3

internal fun yakuTripleTriples(contenedorGrupos: ContenedorGrupos) =
    contenedorGrupos.tris.size == 3

internal fun yakuInterior(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (c !is CartaNumero) return false
            if (c.esExterior()) return false
        }
    }

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (c !is CartaNumero) return false
            if (c.esExterior()) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (c !is CartaNumero) return false
            if (c.esExterior()) return false
        }
    }

    return true
}

// TODO
internal fun yakuDragones(contenedorGrupos: ContenedorGrupos): Int {

    return 0
}

internal fun yakuParUnico(contenedorGrupos: ContenedorGrupos): Boolean {
    val par = contenedorGrupos.pares[0]
    val c1 = par[0]
    val c2 = par[1]

    if (c1 !is CartaNumero || c2 !is CartaNumero) return false

    return c1.valor == c2.valor
}
