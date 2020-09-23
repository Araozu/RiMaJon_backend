package dev.araozu

import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: HashMap<String, WebSocketSession> = HashMap()
    private val ordenJugadores = Array(4) {""}
    private val manos: HashMap<String, Mano> = HashMap()
    private val dora: ArrayList<Int> = arrayListOf()
    private val doraPublico = Array(5) {0}
    private val doraOculto = Array(5) {0}
    var estadoJuego = EstadoJuego.Espera
    var posCartaActual = 0
    var cartasRestantes = 58

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

        for ((idUsuario, _) in usuarios) {
            val cartasL = arrayListOf<Int>()

            for (i in posCartaActual until (posCartaActual + 10)) {
                cartasL.add(cartas[i])
            }
            posCartaActual += 10

            val mano = Mano(cartasL)
            manos[idUsuario] = mano
        }

        var i = 0
        conexiones.forEach { (idUsuario, socket) ->
            ordenJugadores[i] = idUsuario
            i++
            socket.send(Frame.Text("{\"operacion\": \"juego_iniciado\"}"))
        }
        conexiones.clear()
    }

    private suspend fun enviarDatos(idUsuario: String, ws: WebSocketSession) {
        var doraOcultoS = Array(5) {0}
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

        val datosJuego = DatosJuego(doraPublico, doraOcultoS, manosS, cartasRestantes, ordenJugadores)
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

}
