package dev.araozu.juego.yaku

import dev.araozu.juego.ContenedorGrupos

enum class Yaku {
    // 10
    DragonesFull,
    Verde,

    // 7
    RealezaDragones,
    RealezaFull,
    EscaleraPerfecta,
    A10,

    // 5
    EscaleraFull,

    // 3
    TripleTriplesCerrados,
    Exterior,

    // 2
    Escalera,
    Negro,
    Rojo,
    SemiExterior,
    DobleSecuenciaPura,

    // 1
    Dragones,
    Interior,
    TripleTriples,
    Realeza,
    DobleSecuencia,
    ManoCerrada
}

fun obtenerListaYakus(contenedorGrupos: ContenedorGrupos, esManoAbierta: Boolean): ArrayList<Yaku> {
    val listaYakus = ArrayList<Yaku>()

    // Invariante: 3 sequencias/triples/cuadruples, 1 par y ningun huerfano
    if (contenedorGrupos.seqs.size + contenedorGrupos.tris.size != 3
        || contenedorGrupos.pares.size != 1
        || contenedorGrupos.huerfanos.size != 0
    ) {
        System.err.println("Error de invariante: Se intento verificar los yakus de un contenedor invalido.")
        return arrayListOf()
    }

    // 10 puntos
    if (yakuDragonesFull(contenedorGrupos)) {
        listaYakus.add(Yaku.DragonesFull)
        return listaYakus
    }
    if (yakuVerde(contenedorGrupos)) {
        listaYakus.add(Yaku.Verde)
        return listaYakus
    }

    // 7 puntos
    if (yakuRealezaDragones(contenedorGrupos)) {
        listaYakus.add(Yaku.RealezaDragones)
        return listaYakus
    }
    if (yakuRealezaFull(contenedorGrupos)) {
        listaYakus.add(Yaku.RealezaFull)
        return listaYakus
    }

    // 5 puntos
    // TODO

    // 3 puntos
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

    // 2 puntos
    if (yakuSemiExterior(contenedorGrupos)) {
        listaYakus.add(Yaku.SemiExterior)
    }
    if (yakuRojo(contenedorGrupos)) {
        listaYakus.add(Yaku.Rojo)
    }
    if (yakuNegro(contenedorGrupos)) {
        listaYakus.add(Yaku.Negro)
    }
    if (verificarEscalera && yakuEscalera(contenedorGrupos)) {
        listaYakus.add(Yaku.Escalera)
    }

    // 1 punto
    if (yakuDobleSecuencia(contenedorGrupos)) {
        listaYakus.add(Yaku.DobleSecuencia)
    }
    val cantidad = yakuRealeza(contenedorGrupos)
    if (cantidad > 0) {
        listaYakus.add(Yaku.Realeza)
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
    if (!esManoAbierta) {
        listaYakus.add(Yaku.ManoCerrada)
    }

    return listaYakus
}
