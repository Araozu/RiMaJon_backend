package dev.araozu

import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: HashMap<String, WebSocketSession> = HashMap()
    private val ordenJugadores = Array(4) { "" }
    private val manos: HashMap<String, Mano> = HashMap()
    private val dora: ArrayList<Int> = arrayListOf()
    private val doraPublico = Array(5) { 0 }
    private val doraOculto = Array(5) { 0 }
    private var estadoJuego = EstadoJuego.Espera
    private var posCartaActual = 0
    private var turnoActual = 0
    private var turnosHastaDora = 15

    suspend fun iniciarJuego(ws: WebSocketSession) {
        if (estadoJuego != EstadoJuego.Espera) return

        if (conexiones.size < 4) {
            ws.send(Frame.Text("{\"operacion\": \"error\", \"razon\": \"Usuarios insuficientes\"}"))
            return
        }

        estadoJuego = EstadoJuego.Iniciado
        for (i in posCartaActual until (posCartaActual + 10)) {
            dora.add(cartas[i])
        }
        posCartaActual += 10
        doraPublico[0] = dora[0]
        doraOculto[0] = dora[4]

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
                turnosHastaDora--

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
        var doraOcultoS = Array(5) { 0 }
        val manosS = HashMap<String, Mano>()

        for ((idUsuarioAct, mano) in manos) {
            if (idUsuarioAct == idUsuario) {
                if (mano.allIn) {
                    doraOcultoS = doraOculto
                }
                manosS[idUsuarioAct] = mano
            } else {
                manosS[idUsuarioAct] = mano.obtenerManoPrivada()
            }
        }

        val idJugadorTurnoActual = ordenJugadores[turnoActual]
        val datosJuego = DatosJuego(
            doraPublico,
            doraOcultoS,
            manosS,
            108 - posCartaActual,
            ordenJugadores,
            idJugadorTurnoActual,
            turnosHastaDora
        )
        ws.send(Frame.Text("{\"operacion\": \"actualizar_datos\", \"datos\": ${gson.toJson(datosJuego)}}"))
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

            // Extraer, dar sig carta al sig jugador, cambiar turno
            turnoActual = (turnoActual + 1) % 4
            val idSigUsuario = ordenJugadores[turnoActual]
            val sigCarta = cartas[posCartaActual]
            posCartaActual++
            turnosHastaDora--
            manos[idSigUsuario]!!.sigCarta = sigCarta

            // Actualizar dora
            if (turnosHastaDora == 0) {
                val sigPosDora = doraPublico.indexOf(0)
                // Si aun quedan doras
                if (sigPosDora != -1) {
                    doraPublico[sigPosDora] = dora[sigPosDora]
                    turnosHastaDora = 15
                }
                // Si ya no hay doras
                else {
                    turnosHastaDora = 108
                }
            }

            // Enviar datos
            for ((idUsuarioEnvio, ws) in conexiones) {
                val manosS = HashMap<String, Mano>()
                var doraOcultoS = Array(5) { 0 }

                for ((idUsuarioAct, mano) in manos) {
                    when (idUsuarioAct) {
                        idUsuarioEnvio -> {
                            manosS[idUsuarioAct] = mano
                            if (mano.allIn) {
                                doraOcultoS = doraOculto
                            }
                        }
                        idUsuario -> {
                            manosS[idUsuarioAct] = mano.obtenerManoPrivada()
                        }
                        idSigUsuario -> {
                            manosS[idUsuarioAct] = mano.obtenerManoPrivada()
                        }
                    }
                }

                val datosJuego = DatosJuego(
                    doraPublico,
                    doraOcultoS,
                    manosS,
                    108 - posCartaActual,
                    ordenJugadores,
                    ordenJugadores[turnoActual],
                    turnosHastaDora
                )
                ws.send(Frame.Text("{\"operacion\": \"actualizar_manos\", \"datos\": ${gson.toJson(datosJuego)}}"))
            }

        }
    }

}
