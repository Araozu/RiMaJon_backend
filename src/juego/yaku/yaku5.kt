package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos

// TODO: Cambiar descripcion en la pagina web
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

internal fun yakuEscaleraFull(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.seqs.size != 3) return false

    var numeroInicialSemiEscalera = 0
    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (c !is CartaNumero) return false

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

            numeroInicialSemiEscalera += 1
        }
    }

    return false
}

internal fun yakuTripleTriplesCerrados(contenedorGrupos: ContenedorGrupos) =
    contenedorGrupos.tris.size == 3
