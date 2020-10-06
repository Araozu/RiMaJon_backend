package dev.araozu.juego

import dev.araozu.*
import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: HashMap<String, WebSocketSession> = HashMap()
    private val ordenJugadores = Array(4) { "" }
    private val manos: HashMap<String, Mano> = HashMap()
    private var gestorDora: GestorDora? = null
    private var estadoJuego = EstadoJuego.Espera
    private var posCartaActual = 0
    private var turnoActual = 0

    suspend fun iniciarJuego(ws: WebSocketSession) {
        if (estadoJuego != EstadoJuego.Espera) return

        if (conexiones.size < 4) {
            ws.send(Frame.Text("{\"operacion\": \"error\", \"razon\": \"Usuarios insuficientes\"}"))
            return
        }

        estadoJuego = EstadoJuego.Iniciado

        // Inicializar dora
        val dora: ArrayList<Int> = arrayListOf()
        for (i in posCartaActual until (posCartaActual + 10)) {
            dora.add(cartas[i])
        }
        gestorDora = GestorDora(dora)
        posCartaActual += 10

        // Asignar orden de jugadores
        var i = 0
        var idJugadorInicial = ""
        conexiones.forEach { (idUsuario, _) ->
            if (i == 0) idJugadorInicial = idUsuario
            ordenJugadores[i] = idUsuario
            i++

            val cartasL = arrayListOf<Int>()

            for (j in posCartaActual until (posCartaActual + 10)) {
                cartasL.add(cartas[j])
            }
            posCartaActual += 10

            val mano = if (idJugadorInicial == idUsuario) {
                val sigCarta = cartas[posCartaActual]
                posCartaActual++
                gestorDora!!.actualizarDoraCerrado()

                Mano(cartasL, sigCarta = sigCarta)
            } else {
                Mano(cartasL)
            }

            manos[idUsuario] = mano
        }

        conexiones.forEach { (_, socket) ->
            socket.send(Frame.Text("{\"operacion\": \"juego_iniciado\"}"))
        }

        conexiones.clear()
    }

    private suspend fun enviarDatos(idUsuario: String, ws: WebSocketSession) {
        val manosS = HashMap<String, Mano>()

        for ((idUsuarioAct, mano) in manos) {
            if (idUsuarioAct == idUsuario) {
                manosS[idUsuarioAct] = mano
            } else {
                manosS[idUsuarioAct] = mano.obtenerManoPrivada()
            }
        }

        val idJugadorTurnoActual = ordenJugadores[turnoActual]
        val (doraCerrado, doraAbierto) = gestorDora!!
        val datosJuego = DatosJuego(
            doraCerrado,
            doraAbierto,
            manosS,
            108 - posCartaActual,
            ordenJugadores,
            idJugadorTurnoActual,
            gestorDora!!.turnosRestantesDoraCerrado
        )
        ws.send(Frame.Text("{\"operacion\": \"actualizar_datos\", \"datos\": ${gson.toJson(datosJuego)}}"))
    }

    private suspend fun enviarDatosATodos() {
        for ((idUsuario, ws) in conexiones) {
            enviarDatos(idUsuario, ws)
        }
    }

    suspend fun agregarConexion(idUsuario: String, conexion: WebSocketSession) {
        conexiones[idUsuario] = conexion
        if (estadoJuego == EstadoJuego.Iniciado) {
            enviarDatos(idUsuario, conexion)
        }
    }

    fun agregarUsuario(idUsuario: String) {
        if (estadoJuego == EstadoJuego.Espera) usuarios.add(Pair(idUsuario, true))
    }

    private fun cambiarTurnoSigJugadorConsecutivo() {
        // Extraer, dar sig carta al sig jugador, cambiar turno
        turnoActual = (turnoActual + 1) % 4
        val idSigUsuario = ordenJugadores[turnoActual]
        val sigCarta = cartas[posCartaActual]
        posCartaActual++
        manos[idSigUsuario]!!.sigCarta = sigCarta
    }

    suspend fun manejarDescarte(idUsuario: String, carta: Int) {
        if (ordenJugadores[turnoActual] == idUsuario) {
            val m = manos[idUsuario]!!

            if (m.sigCarta == carta) {
                m.sigCarta = -1
            } else {
                val posCarta = m.cartas.indexOf(carta)
                if (posCarta != -1) {
                    m.cartas.removeAt(posCarta)
                    m.cartas.add(m.sigCarta)
                    m.sigCarta = -1
                } else {
                    return
                }
            }

            m.descartes.add(carta)

            // Verificar seq/tri/quad/win
            var hayOportunidades = false
            for ((_, mano) in manos) {
                val oportunidadSeq = OportunidadSeq.verificar(carta, mano.cartas)
                if (oportunidadSeq != null) {
                    hayOportunidades = true
                    mano.oportunidades.add(oportunidadSeq)
                }
            }

            if (hayOportunidades) {
                // Enviar datos
                enviarDatosATodos()
            } else {
                cambiarTurnoSigJugadorConsecutivo()

                // Actualizar dora
                gestorDora!!.actualizarDoraCerrado()

                // Enviar datos
                enviarDatosATodos()
            }
        }
    }

}
