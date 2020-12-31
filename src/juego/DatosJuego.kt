package dev.araozu.juego

data class DatosJuego(
    val dora: ArrayList<Int>,
    val manos: HashMap<String, Mano>,
    val cartasRestantes: Int,
    val ordenJugadores: Array<String>,
    val turnoActual: String,
    val turnosHastaDora: Int,
    val dragonPartida: Dragon,
    val estadoJuego: EstadoJuego
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatosJuego

        if (dora != other.dora) return false
        if (manos != other.manos) return false
        if (cartasRestantes != other.cartasRestantes) return false
        if (!ordenJugadores.contentEquals(other.ordenJugadores)) return false
        if (turnoActual != other.turnoActual) return false
        if (turnosHastaDora != other.turnosHastaDora) return false
        if (dragonPartida != other.dragonPartida) return false
        if (estadoJuego != other.estadoJuego) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dora.hashCode()
        result = 31 * result + manos.hashCode()
        result = 31 * result + cartasRestantes
        result = 31 * result + ordenJugadores.contentHashCode()
        result = 31 * result + turnoActual.hashCode()
        result = 31 * result + turnosHastaDora
        result = 31 * result + dragonPartida.hashCode()
        result = 31 * result + estadoJuego.hashCode()
        return result
    }
}
