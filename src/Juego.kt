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
    private var turnosHastaDora = 27 // 27 17 7 3

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

    private fun arrContiene(v1: Int, v2: Int, arr: List<Int>): Boolean {
        var v1E = false
        var v2E = false
        for (i in arr) {
            if (i == v1) v1E = true
            if (i == v2) v2E = true

            if (v1E && v2E) return true
        }

        return false
    }

    private fun verificarTri(carta: Int): HashMap<String, ArrayList<String>>? {
        // La carta es dragon o rey
        if (carta > 54) return null

        val idSigJugador = ordenJugadores[(turnoActual + 1) % 4]
        val manoJugador = manos[idSigJugador]!!
        val cartasJugador = manoJugador.cartas

        val obtValorCarta = { valor: Int -> (valor shl 27) ushr 28 }
        val obtTipoCarta = { valor: Int -> (valor shl 23) ushr 28 }

        val valorCarta = obtValorCarta(carta)
        val cartasAComparar = {
            val filtro = obtTipoCarta(carta)
            cartasJugador.filter { obtTipoCarta(it) == filtro } .map(obtValorCarta)
        }()

        val oportunidades = HashMap<String, ArrayList<String>>()

        val oportunidadesJugador = ArrayList<String>()
        // Primer caso: Xoo
        if (arrContiene(valorCarta + 1, valorCarta + 2, cartasAComparar)) {
            oportunidadesJugador.add("seq")
        }

        // Segundo caso: oXo
        if (arrContiene(valorCarta - 1, valorCarta + 1, cartasAComparar)) {
            oportunidadesJugador.add("seq")
        }

        // Tercer caso: ooX
        if (arrContiene(valorCarta - 1, valorCarta - 2, cartasAComparar)) {
            oportunidadesJugador.add("seq")
        }

        oportunidades[idSigJugador] = oportunidadesJugador

        return if (oportunidades.isNotEmpty()) oportunidades else null
    }

    private suspend fun enviarOportunidades(oportunidades: HashMap<String, ArrayList<String>>, cartaDescartada: Int) {
        for ((id, ops) in oportunidades) {
            val oportunidadesL = OportunidadesJuego(ops, cartaDescartada)
            conexiones[id]!!.send(Frame.Text("{\"operacion\": \"oportunidad\", \"datos\": ${gson.toJson(oportunidadesL)}}"))
        }
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
            val oportunidades = verificarTri(carta)
            if (oportunidades != null) {
                enviarOportunidades(oportunidades, carta)
                return
            }

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
