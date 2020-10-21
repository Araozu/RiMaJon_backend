package dev.araozu.juego.yaku

import dev.araozu.juego.CartaNumero
import dev.araozu.juego.ContenedorGrupos

internal fun yakuSemiExterior(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        var terminales = 0
        for (c in carrl) {
            if (c.esDragonORey() || (c is CartaNumero && c.esExterior())) {
                terminales += 1
            }
        }
        if (terminales == 0) return false
    }

    for (carrl in contenedorGrupos.tris) {
        var terminales = 0
        for (c in carrl) {
            if (c.esDragonORey() || (c is CartaNumero && c.esExterior())) {
                terminales += 1
            }
        }
        if (terminales == 0) return false
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            var terminales = 0
            if (c.esDragonORey() || (c is CartaNumero && c.esExterior())) {
                terminales += 1
            }
            if (terminales == 0) return false
        }
    }

    return true
}

internal fun yakuRojo(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (!c.esCartaRoja()) return false
        }
    }

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (!c.esCartaRoja()) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (!c.esCartaRoja()) return false
        }
    }

    return true
}

internal fun yakuNegro(contenedorGrupos: ContenedorGrupos): Boolean {

    for (carrl in contenedorGrupos.seqs) {
        for (c in carrl) {
            if (!c.esCartaNegra()) return false
        }
    }

    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (!c.esCartaNegra()) return false
        }
    }

    for (carrl in contenedorGrupos.pares) {
        for (c in carrl) {
            if (!c.esCartaNegra()) return false
        }
    }

    return true
}

internal fun yakuTripleCuadruples(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.tris.size != 3) return false

    for (triple in contenedorGrupos.tris) {
        if (triple.size != 4) return false
    }

    return true
}

internal fun yakuEscalera(contenedorGrupos: ContenedorGrupos): Boolean {

    if (contenedorGrupos.seqs.size != 3) return false

    var primeraCarta = false
    var numeroActual = 0
    var colorCarta = ""
    for (carrl in contenedorGrupos.tris) {
        for (c in carrl) {
            if (c !is CartaNumero) return false

            if (!primeraCarta) {
                colorCarta = c.color
                numeroActual = when (c.numero) {
                    1 -> 2
                    2 -> 3
                    else -> return false
                }

                primeraCarta = true
            } else {
                if (c.numero != numeroActual) return false
                if (c.color != colorCarta) return false

                numeroActual += 1
            }

        }
    }

    return true
}

