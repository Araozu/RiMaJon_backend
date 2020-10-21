package dev.araozu.juego.yaku

import dev.araozu.juego.CartaRealeza
import dev.araozu.juego.ContenedorGrupos

internal fun yakuRealezaDragones(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.tris.size != 3) return false

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (!c.esDragonORey()) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (!c.esDragonORey()) return false
        }
    }

    return true
}

internal fun yakuRealezaFull(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.tris.size != 3) return false

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (c !is CartaRealeza) return false
        }
    }

    return false
}
