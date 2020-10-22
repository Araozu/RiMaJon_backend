package dev.araozu.juego.yaku

import dev.araozu.juego.ContenedorGrupos

enum class Yaku {
    // 15
    DragonesFull,
    Verde,

    // 10
    RealezaDragones,
    RealezaFull,

    // 5
    TripleTriplesCerrados,
    EscaleraFull,
    Exterior,

    // 4
    Escalera,
    TripleCuadruples,
    Negro,
    Rojo,
    SemiExterior,

    // 3
    ParUnico,
    Dragones,
    Interior,
    TripleTriples,
    TripleSecuenciaCerrada,
    Realeza,
    DobleSecuenciaPura
}

fun obtenerListaYakus(contenedorGrupos: ContenedorGrupos, esManoAbierta: Boolean): ArrayList<Yaku> {
    val listaYakus = ArrayList<Yaku>()

    // Invariante: 3 sequencias/triples/cuadruples, 1 par y ningun huerfano
    if (contenedorGrupos.seqs.size + contenedorGrupos.tris.size != 3
        || contenedorGrupos.pares.size != 1
        || contenedorGrupos.huerfanos.size != 0
    ) {
        throw Error("Error de invariante: Se intento verificar los yakus de un contenedor invalido.")
    }

    // 15 puntos
    if (yakuDragonesFull(contenedorGrupos)) {
        listaYakus.add(Yaku.DragonesFull)
        return listaYakus
    }
    if (yakuVerde(contenedorGrupos)) {
        listaYakus.add(Yaku.Verde)
        return listaYakus
    }

    // 10 puntos
    if (yakuRealezaDragones(contenedorGrupos)) {
        listaYakus.add(Yaku.RealezaDragones)
        return listaYakus
    }
    if (yakuRealezaFull(contenedorGrupos)) {
        listaYakus.add(Yaku.RealezaFull)
        return listaYakus
    }

    // 5 puntos
    if (yakuExterior(contenedorGrupos)) {
        listaYakus.add(Yaku.Exterior)
    }

    var verificarEscalera = true
    if (yakuEscaleraFull(contenedorGrupos)) {
        listaYakus.add(Yaku.EscaleraFull)
        verificarEscalera = false
    }

    // Triple triples cerrados
    var verificarTripleTriples = true
    if (!esManoAbierta && yakuTripleTriples(contenedorGrupos)) {
        listaYakus.add(Yaku.TripleTriplesCerrados)
        listaYakus.add(Yaku.TripleTriples)
        verificarTripleTriples = false
    }

    // 4 puntos
    if (yakuSemiExterior(contenedorGrupos)) {
        listaYakus.add(Yaku.SemiExterior)
    }
    if (yakuRojo(contenedorGrupos)) {
        listaYakus.add(Yaku.Rojo)
    }
    if (yakuNegro(contenedorGrupos)) {
        listaYakus.add(Yaku.Negro)
    }
    if (yakuTripleCuadruples(contenedorGrupos)) {
        listaYakus.add(Yaku.TripleCuadruples)
    }
    if (verificarEscalera && yakuEscalera(contenedorGrupos)) {
        listaYakus.add(Yaku.Escalera)
    }

    // 3 puntos
    if (yakuDobleSecuenciaPura(contenedorGrupos)) {
        listaYakus.add(Yaku.DobleSecuenciaPura)
    }
    val cantidad = yakuRealeza(contenedorGrupos)
    if (cantidad > 0) {
        listaYakus.add(Yaku.Realeza)
    }
    if (!esManoAbierta && yakuTripleSecuenciaCerrada(contenedorGrupos)) {
        listaYakus.add(Yaku.TripleSecuenciaCerrada)
    }
    if (verificarTripleTriples && yakuTripleTriples(contenedorGrupos)) {
        listaYakus.add(Yaku.TripleTriples)
    }
    if (yakuInterior(contenedorGrupos)) {
        listaYakus.add(Yaku.Interior)
    }
    val cantidadDragon = yakuDragones(contenedorGrupos)
    if (cantidadDragon > 0) {
        listaYakus.add(Yaku.Dragones)
    }
    if (yakuParUnico(contenedorGrupos)) {
        listaYakus.add(Yaku.ParUnico)
    }

    return listaYakus
}
