package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos

// TODO
internal fun yakuDobleSecuencia(contenedorGrupos: ContenedorGrupos): Boolean {

    return false
}

internal fun yakuVariedad(contenedorGrupos: ContenedorGrupos): Boolean {

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

