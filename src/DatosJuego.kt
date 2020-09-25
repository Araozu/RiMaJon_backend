package dev.araozu

data class DatosJuego(
    val dora: Array<Int>,
    val doraOculto: Array<Int>,
    val manos: HashMap<String, Mano>,
    val cartasRestantes: Int,
    val ordenJugadores: Array<String>,
    val turnoActual: String,
    val turnosHastaDora: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatosJuego

        if (!dora.contentEquals(other.dora)) return false
        if (!doraOculto.contentEquals(other.doraOculto)) return false
        if (manos != other.manos) return false
        if (cartasRestantes != other.cartasRestantes) return false
        if (!ordenJugadores.contentEquals(other.ordenJugadores)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dora.contentHashCode()
        result = 31 * result + doraOculto.contentHashCode()
        result = 31 * result + manos.hashCode()
        result = 31 * result + cartasRestantes
        result = 31 * result + ordenJugadores.contentHashCode()
        return result
    }

}