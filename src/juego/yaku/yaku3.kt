package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos

internal fun yakuExterior(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.tris.size != 3) return false

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (!c.esDragonORey() || c !is CartaNumero || !c.esExterior())
                return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (!c.esDragonORey() || c !is CartaNumero || !c.esExterior())
                return false
        }
    }

    return true
}

internal fun yakuTripleTriplesCerrados(contenedorGrupos: ContenedorGrupos): Boolean {

    return false
}

