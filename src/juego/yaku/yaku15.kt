package dev.araozu.juego.yaku

import dev.araozu.juego.CartaDragon
import dev.araozu.juego.ContenedorGrupos

internal fun yakuDragonesFull(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (c !is CartaDragon) return false
        }
    }

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (c !is CartaDragon) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (c !is CartaDragon) return false
        }
    }

    return true
}

internal fun yakuVerde(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (!c.esCartaVerde()) return false
        }
    }

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (!c.esCartaVerde()) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (!c.esCartaVerde()) return false
        }
    }

    return true
}
