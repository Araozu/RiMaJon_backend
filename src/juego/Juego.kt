package dev.araozu.juego

import dev.araozu.*
import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: HashMap<String, WebSocketSession> = HashMap()
    private val ordenJugadores = Array(4) { "" }
    private val manos: HashMap<String, Mano> = HashMap()
    private var gestorDora = GestorDora(cartas)
    private var estadoJuego = EstadoJuego.Espera
    private var posCartaActual = 10
    private var posJugadorActual = 0
    private var dragonPartida = Dragon.Negro
    private var oportunidadesRestantes = 0

    suspend fun iniciarJuego(ws: WebSocketSession) {
        if (estadoJuego != EstadoJuego.Espera) return

        if (conexiones.size < 4) {
            ws.send(Frame.Text("{\"operacion\": \"error\", \"razon\": \"Usuarios insuficientes\"}"))
            return
        }

        estadoJuego = EstadoJuego.Iniciado

        // Asignar orden de jugadores
        var i = 0
        val posInicio = (Math.random() * 4).toInt()
        conexiones.forEach { (idUsuario, _) ->
            ordenJugadores[i] = idUsuario
            val dragonActual = Dragon.get(i)

            val cartasL = arrayListOf<Int>()

            for (j in posCartaActual until (posCartaActual + 10)) {
                cartasL.add(cartas[j])
            }
            posCartaActual += 10

            val mano = if (i == posInicio) {
                val sigCarta = cartas[posCartaActual]
                posCartaActual++
                gestorDora.actualizarDora()
                dragonPartida = dragonActual
                posJugadorActual = i

                Mano(cartasL, sigCarta = sigCarta, dragon = dragonActual)
            } else {
                Mano(cartasL, dragon = dragonActual)
            }

            manos[idUsuario] = mano
            i++
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

        val idJugadorTurnoActual = ordenJugadores[posJugadorActual]
        val doraCerrado = gestorDora.dora
        val datosJuego = DatosJuego(
            doraCerrado,
            manosS,
            108 - posCartaActual,
            ordenJugadores,
            idJugadorTurnoActual,
            gestorDora.turnosRestantesDora,
            dragonPartida,
            oportunidadesRestantes
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
        // Cambiar turno al sig jugador consecutivo
        posJugadorActual = (posJugadorActual + 1) % 4
        oportunidadesRestantes = 0

        val idSigUsuario = ordenJugadores[posJugadorActual]

        // Extraer sig carta. TODO: Verificar que no quedan cartas y establecer empate
        val sigCarta = cartas[posCartaActual]
        posCartaActual++

        // Asignar nueva carta
        val manoSigJugador = manos[idSigUsuario]!!
        manoSigJugador.sigCarta = sigCarta

        // TODO: Arreglar. Roto.
        val oportunidadWin = OportunidadWin.verificar(sigCarta, manoSigJugador.cartas, manoSigJugador.cartasReveladas)
        if (oportunidadWin != null) {
            manoSigJugador.oportunidades.add(oportunidadWin)
        }

    }

    private fun esUsuarioIzq(idUsuarioIzq: String, idUsuario1: String): Boolean {
        var posUsuario1 = 0
        var posUsuarioIzq = 0
        for ((posActual, idUsuario) in ordenJugadores.withIndex()) {
            if (idUsuario == idUsuario1) posUsuario1 = posActual
            if (idUsuario == idUsuarioIzq) posUsuarioIzq = posActual
        }
        return (posUsuarioIzq + 1) % 4 == posUsuario1
    }

    suspend fun manejarDescarte(idUsuario: String, carta: Int) {
        if (ordenJugadores[posJugadorActual] != idUsuario) return

        // Si el jugador del turno actual ya descarto, otros jugadores tienen oportunidades
        // e intento descartar de nuevo
        if (oportunidadesRestantes > 0) return

        val m = manos[idUsuario]!!

        if (m.sigCarta == carta) {
            m.sigCarta = -1
        } else {
            val posCarta = m.cartas.indexOf(carta)
            if (posCarta != -1) {
                m.cartas.removeAt(posCarta)

                // Tras llamar un Seq/Tri el jugador no tiene una carta adicional en su mano.
                if (m.sigCarta != -1) m.cartas.add(m.sigCarta)

                m.sigCarta = -1
            } else {
                return
            }
        }

        m.descartes.add(carta)

        // Verificar seq/tri/win
        var hayOportunidades = false
        for ((idUsuarioActual, mano) in manos) {
            // No buscar oportunidades en el usuario que acaba de descartar.
            if (idUsuarioActual == idUsuario) continue

            // Solo verificar seq en el jugador a la derecha del que descarto
            if (esUsuarioIzq(idUsuario, idUsuarioActual)) {
                val oportunidadSeq = OportunidadSeq.verificar(carta, mano.cartas)
                if (oportunidadSeq != null) {
                    hayOportunidades = true
                    oportunidadesRestantes++
                    mano.oportunidades.add(oportunidadSeq)
                }
            }

            // Oportunidades tri
            val oportunidadTri = OportunidadTri.verificar(carta, mano.cartas)
            if (oportunidadTri != null) {
                hayOportunidades = true
                oportunidadesRestantes++
                mano.oportunidades.add(oportunidadTri)
            }

            // Oportunidades win (ron)
            val oportunidadWin = OportunidadWin.verificar(carta, mano.cartas, mano.cartasReveladas)
            if (oportunidadWin != null) {
                hayOportunidades = true
                oportunidadesRestantes++
                mano.oportunidades.add(oportunidadWin)
            }
        }

        if (hayOportunidades) {
            // Enviar datos
            enviarDatosATodos()
        } else {
            cambiarTurnoSigJugadorConsecutivo()

            // Actualizar dora
            gestorDora.actualizarDora()

            // Enviar datos
            enviarDatosATodos()
        }
    }

    // TODO: Usar diferente metodo para ignorar oportunidad Tsumo
    suspend fun ignorarOportunidadSeq(idUsuario: String) {

        var aunHayOportunidades = false
        oportunidadesRestantes--

        for ((id, mano) in manos) {
            // Eliminar oportunidad del usuario
            if (id == idUsuario) {
                mano.oportunidades = arrayListOf()
                enviarDatos(id, conexiones[id]!!)
                continue
            }

            // Si algun otro jugador tiene una oportunidad
            if (mano.oportunidades.isNotEmpty()) {
                aunHayOportunidades = true
            }
        }

        // Si no quedan oportunidades cambiar el turno al sig jugador
        if (!aunHayOportunidades) {
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
        val manoJugadorDescarte = manos[ordenJugadores[posJugadorActual]]!!
        val descartesJ = manoJugadorDescarte.descartes

        // La carta solicitada para robar es invalida
        if (descartesJ[descartesJ.size - 1] != cartaDescartada) {
            println("La carta a robar es invalida")
            return
        }

        descartesJ.removeAt(descartesJ.size - 1)

        val manoRobador = manos[idUsuario]!!
        val cartasRobador = manoRobador.cartas
        val (vCarta1, vCarta2) = combinacion

        // El jugador no tiene las cartas con las que formar seq
        if (!cartasRobador.contains(vCarta1) || !cartasRobador.contains(vCarta2)) {
            println("El jugador no tiene las cartas que dice que tiene: $vCarta1, $vCarta2")
            return
        }

        // Quitar cartas de la mano y moverlas a cartas reveladas
        cartasRobador.remove(vCarta1)
        cartasRobador.remove(vCarta2)
        val seq = arrayListOf(cartaDescartada, vCarta1, vCarta2)
        seq.sort()
        manoRobador.cartasReveladas.add(seq)

        // Eliminar las oportunidades
        manoRobador.oportunidades = arrayListOf()
        oportunidadesRestantes = 0

        // Cambiar turno al robador sin dar carta
        // turnoActual = (turnoActual + 1) % 4
        cambiarTurnoSegunIdUsuario(idUsuario)

        enviarDatosATodos()
    }

}
