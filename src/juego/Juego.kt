package dev.araozu.juego

import dev.araozu.*
import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()

    internal var jugadores = Array<Jugador>(4) { JugadorBot(this, "Bot $it") }
    private var ordenJugadores = Array(4) { jugadores[it].idUsuario }

    private var gestorDora = GestorDora(cartas)
    private var estadoJuego = EstadoJuego.Espera
    private var posCartaActual = 10
    private var posJugadorActual = 0
    private var dragonPartida = Dragon.Negro
    private var oportunidadesRestantes = 0

    suspend fun iniciarJuego() {
        if (estadoJuego != EstadoJuego.Espera) return

        estadoJuego = EstadoJuego.Iniciado

        val nuevoArrJugadores = Array<Jugador>(4) { JugadorBot(this, "-") }
        val jugadoresRestantes = arrayListOf(0, 1, 2, 3)

        for (i in 0 until 4) {
            val nuevoIndice = (Math.random() * jugadoresRestantes.size).toInt()
            nuevoArrJugadores[i] = jugadores[nuevoIndice]
            jugadoresRestantes.remove(nuevoIndice)
        }

        dragonPartida = Dragon.get((Math.random() * 4).toInt())

        for ((i, jugador) in nuevoArrJugadores.withIndex()) {
            val cartasL = arrayListOf<Int>()
            for (j in posCartaActual until (posCartaActual + 10)) {
                cartasL.add(cartas[j])
            }
            posCartaActual += 10

            jugador.inicializarCartas(cartasL)
            jugador.inicializarDragon(Dragon.get(i))
            jugador.send(Frame.Text("{\"operacion\": \"juego_iniciado\"}"))
        }

        jugadores = nuevoArrJugadores
        ordenJugadores = Array(4) { jugadores[it].idUsuario }
    }

    private fun obtenerDatosJuegoActuales(): DatosJuego {
        val idJugadorTurnoActual = jugadores[posJugadorActual].idUsuario
        return DatosJuego(
            dora = arrayListOf(),
            manos = hashMapOf(),
            cartasRestantes = 108 - posCartaActual,
            ordenJugadores = ordenJugadores,
            turnoActual = idJugadorTurnoActual,
            turnosHastaDora = gestorDora.turnosRestantesDora,
            dragonPartida = dragonPartida,
            oportunidadesRestantes = oportunidadesRestantes
        )
    }

    private suspend fun enviarDatosATodos() {
        val datosJuego = obtenerDatosJuegoActuales()

        jugadores.forEach { it.enviarDatos(datosJuego) }
    }

    suspend fun agregarConexion(idUsuario: String, conexion: WebSocketSession) {
        if (estadoJuego != EstadoJuego.Espera) return

        // Buscar si el jugador ya existia
        jugadores.forEach {
            if (it.idUsuario == idUsuario) {
                it.actualizarConexion(conexion)
                it.enviarDatos(obtenerDatosJuegoActuales())
                return
            }
        }

        // El jugador es nuevo. Asignarlo.
        val nuevoJugador = JugadorHumano(this, idUsuario, conexion)
        for (i in 0 until 4) {
            if (jugadores[i] !is JugadorBot) {
                jugadores[i] = nuevoJugador
                break
            }
        }
    }

    fun agregarUsuario(idUsuario: String) {
        if (estadoJuego == EstadoJuego.Espera) usuarios.add(Pair(idUsuario, true))
    }

    private fun cambiarTurnoSigJugadorConsecutivo() {
        // Cambiar turno al sig jugador consecutivo
        posJugadorActual = (posJugadorActual + 1) % 4
        oportunidadesRestantes = 0

        // Si se acabaron las cartas
        if (posCartaActual >= cartas.size) {
            estadoJuego = EstadoJuego.Terminado
            return
        }

        // Sino
        val sigCarta = cartas[posCartaActual]
        posCartaActual++

        // Asignar carta
        jugadores[posJugadorActual].recibirCarta(sigCarta)
        // Verificar Tsumo
        jugadores[posJugadorActual].verificarTsumo()
    }

    suspend fun manejarDescarte(idUsuario: String, cartaDescartada: Int) {
        // Si un jugador del que no es turno intenta descartar
        if (jugadores[posJugadorActual].idUsuario != idUsuario) return

        // Si el jugador del turno actual ya descarto, otros jugadores tienen oportunidades
        // e intento descartar de nuevo
        if (oportunidadesRestantes > 0) return

        val cantidadOportunidades = jugadores[posJugadorActual].descartarCarta(cartaDescartada)

        when {
            cantidadOportunidades > 0 -> {
                // Enviar datos
                enviarDatosATodos()
            }
            cantidadOportunidades == 0 -> {
                cambiarTurnoSigJugadorConsecutivo()

                // Actualizar dora
                gestorDora.actualizarDora()

                // Enviar datos
                enviarDatosATodos()
            }
            else -> {
                System.err.println("Se intento descartar en un estado invalido.")
            }
        }
    }

    suspend fun ignorarOportunidades(idUsuario: String) {
        val jugador = jugadores.find { it.idUsuario == idUsuario } ?: return
        val aunHayOportunidades = jugador.ignorarOportunidades()

        // Si no quedan oportunidades cambiar el turno al sig jugador
        if (aunHayOportunidades) {
            jugador.enviarDatos(obtenerDatosJuegoActuales())
        } else {
            cambiarTurnoSigJugadorConsecutivo()

            // Actualizar dora
            gestorDora.actualizarDora()

            // Enviar los nuevos datos
            enviarDatosATodos()
        }
    }

    private fun cambiarTurnoSegunIdUsuario(idUsuario: String) {
        for ((posJugador, i) in ordenJugadores.withIndex()) {
            if (i == idUsuario) {
                posJugadorActual = posJugador
                break
            }
        }
    }

    suspend fun manejarSeqTri(idUsuario: String, cartaDescartada: Int, combinacion: Pair<Int, Int>) {
        val jugadorOportunidad = jugadores.find { it.idUsuario == idUsuario } ?: return
        val jugadorDescate = jugadores[posJugadorActual]

        val roboExitoso = jugadorOportunidad.manejarTriSeq(jugadorDescate, cartaDescartada, combinacion)

        if (!roboExitoso) {
            return
        }

        // Eliminar oportunidades del resto. TODO: Implementar prioridad: Win -> Tri -> Seq
        jugadores.forEach { it.ignorarOportunidades() }

        // Cambiar turno al robador sin dar carta
        // turnoActual = (turnoActual + 1) % 4
        cambiarTurnoSegunIdUsuario(idUsuario)
        enviarDatosATodos()
    }

}
