package dev.araozu.juego

class ContenedorGrupos(
    private val tris: ArrayList<ArrayList<Carta>>,
    private val seqs: ArrayList<ArrayList<Carta>>,
    private val pares: ArrayList<ArrayList<Carta>>,
    private val huerfanos: ArrayList<Carta>
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
        pares.size == 1 && huerfanos.size == 0

}
