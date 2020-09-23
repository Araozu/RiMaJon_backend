package dev.araozu

import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: HashMap<String, WebSocketSession> = HashMap()
    private val manos: HashMap<String, Mano> = HashMap()
    private val dora: ArrayList<Int> = arrayListOf()
    var estadoJuego = EstadoJuego.Espera
    var posCartaActual = 0
    var doraDescubiertos = 1

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

        for ((idUsuario, _) in usuarios) {
            val cartasL = arrayListOf<Int>()

            for (i in posCartaActual until (posCartaActual + 10)) {
                cartasL.add(cartas[i])
            }
            posCartaActual += 10

            val mano = Mano(cartasL)
            manos[idUsuario] = mano
        }

        conexiones.forEach { (_, socket) ->
            socket.send(Frame.Text("{\"operacion\": \"juego_iniciado\"}"))
        }
        conexiones.clear()
        println("Parametros del juego creados!")
    }

    fun agregarConexion(idUsuario: String, conexion: WebSocketSession) {
        conexiones[idUsuario] = conexion
    }

    fun agregarUsuario(idUsuario: String) {
        usuarios.add(Pair(idUsuario, true))
    }

}
