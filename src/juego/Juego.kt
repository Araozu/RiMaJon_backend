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
    private var dragonPartida = Dragon.Negro

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
            val dragonActual = Dragon.get(i)

            val mano = if (idJugadorInicial == idUsuario) {
                val sigCarta = cartas[posCartaActual]
                posCartaActual++
                gestorDora!!.actualizarDoraCerrado()
                dragonPartida = dragonActual

                Mano(cartasL, sigCarta = sigCarta, dragon = dragonActual)
            } else {
                Mano(cartasL, dragon = dragonActual)
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
            gestorDora!!.turnosRestantesDoraCerrado,
            dragonPartida
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
        turnoActual = (turnoActual + 1) % 4

        val idSigUsuario = ordenJugadores[turnoActual]

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
        if (ordenJugadores[turnoActual] != idUsuario) return

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

        // Verificar seq/tri/quad/win
        var hayOportunidades = false
        for ((idUsuarioActual, mano) in manos) {
            // No buscar oportunidades en el usuario que acaba de descartar.
            if (idUsuarioActual == idUsuario) continue

            // Solo verificar seq en el jugador a la derecha del que descarto
            if (esUsuarioIzq(idUsuario, idUsuarioActual)) {
                val oportunidadSeq = OportunidadSeq.verificar(carta, mano.cartas)
                if (oportunidadSeq != null) {
                    hayOportunidades = true
                    mano.oportunidades.add(oportunidadSeq)
                }
            }

            // Oportunidades tri
            val oportunidadTri = OportunidadTri.verificar(carta, mano.cartas)
            if (oportunidadTri != null) {
                hayOportunidades = true
                mano.oportunidades.add(oportunidadTri)
            }

            // Oportunidades win (ron)
            val oportunidadWin = OportunidadWin.verificar(carta, mano.cartas, mano.cartasReveladas)
            if (oportunidadWin != null) {
                hayOportunidades = true
                mano.oportunidades.add(oportunidadWin)
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

    // TODO: Usar diferente metodo para ignorar oportunidad Tsumo
    suspend fun ignorarOportunidadSeq(idUsuario: String) {

        var aunHayOportunidades = false
        for ((id, mano) in manos) {
            // Eliminar oportunidad del usuario
            if (id == idUsuario) {
                mano.oportunidades = arrayListOf()
                continue
            }

            // TODO: Notificar al jugador que su oportunidad ha sido ignorada
            // Si algun otro jugador tiene una oportunidad
            if (mano.oportunidades.isNotEmpty()) {
                aunHayOportunidades = true
            }
        }

        // Si no quedan oportunidades cambiar el turno al sig jugador
        if (!aunHayOportunidades) {
            cambiarTurnoSigJugadorConsecutivo()

            // Actualizar dora
            gestorDora!!.actualizarDoraCerrado()

            // Enviar los nuevos datos
            enviarDatosATodos()
        }
    }

    private fun cambiarTurnoSegunIdUsuario(idUsuario: String) {
        for ((posJugador, i) in ordenJugadores.withIndex()) {
            if (i == idUsuario) {
                turnoActual = posJugador
                break
            }
        }
    }

    suspend fun manejarSeqTri(idUsuario: String, cartaDescartada: Int, combinacion: Pair<Int, Int>) {
        val manoJugadorDescarte = manos[ordenJugadores[turnoActual]]!!
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

        // Cambiar turno al robador sin dar carta
        // turnoActual = (turnoActual + 1) % 4
        cambiarTurnoSegunIdUsuario(idUsuario)

        enviarDatosATodos()
    }

}
