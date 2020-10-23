package dev.araozu.juego

class ContenedorGrupos(
    val tris: ArrayList<ArrayList<Carta>>,
    val seqs: ArrayList<ArrayList<Carta>>,
    val pares: ArrayList<ArrayList<Carta>>,
    val huerfanos: ArrayList<Carta>
) {

    constructor () : this(
        arrayListOf<ArrayList<Carta>>(),
        arrayListOf<ArrayList<Carta>>(),
        arrayListOf<ArrayList<Carta>>(),
        arrayListOf<Carta>()
    ) {
    }

    fun agregarDesdeContenedor(c: ContenedorGrupos) {
        tris.addAll(c.tris)
        seqs.addAll(c.seqs)
        pares.addAll(c.pares)
        huerfanos.addAll(c.huerfanos)
    }

    fun agregarTriDesdeInt(t: ArrayList<Int>) {
        val arrl = ArrayList<Carta>(t.size)

        t.forEach { arrl.add(Carta.obtenerCartaEspecifica(it)) }

        tris.add(arrl)
    }

    fun agregarSeqDesdeInt(t: ArrayList<Int>) {
        val arrl = ArrayList<Carta>(t.size)

        t.forEach { arrl.add(Carta.obtenerCartaEspecifica(it)) }

        seqs.add(arrl)
    }

    fun agregarSeqs(s: ArrayList<ArrayList<Carta>>) {
        seqs.addAll(s)
    }

    fun agregarTris(a: ArrayList<ArrayList<Carta>>) {
        tris.addAll(a)
    }

    fun agregarPares(a: ArrayList<ArrayList<Carta>>) {
        pares.addAll(a)
    }

    fun agregarHuerfanos(a: ArrayList<out Carta>) {
        for (c in a) {
            huerfanos.add(c)
        }
    }

    fun heuristic(): Int =
        tris.size + seqs.size + (if (pares.size <= 1) 1 else 0) - huerfanos.size

    fun estaListo(): Boolean =
        pares.size == 1 && huerfanos.size == 0 && (tris.size + seqs.size == 3)

}
