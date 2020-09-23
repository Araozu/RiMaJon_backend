package dev.araozu

import io.ktor.http.cio.websocket.*

class Juego(val usuarios: ArrayList<Pair<String, Boolean>>) {

    private val cartas: Array<Int> = GestorJuegos.generarCartas()
    val conexiones: ArrayList<WebSocketSession> = arrayListOf()
    private val manos: HashMap<String, Mano> = HashMap()
    private val dora: ArrayList<Int> = arrayListOf()
    var estadoJuego = EstadoJuego.Espera
    var posCartaActual = 0

    suspend fun iniciarJuego() {
        if (estadoJuego != EstadoJuego.Espera) return

        estadoJuego = EstadoJuego.Iniciado
        for (i in posCartaActual until (posCartaActual + 10)) {
            dora.add(cartas[i])
        }
        posCartaActual += 10

        for ((idUsuario, _) in usuarios) {
            val cartas = ArrayList<Int>()

            for (i in posCartaActual until (posCartaActual + 10)) {
                cartas.add(cartas[i])
            }
            posCartaActual += 10

            val mano = Mano(cartas)
            manos[idUsuario] = mano
        }

        conexiones.forEach { socket ->
            socket.send(Frame.Text("{\"operacion\": \"juego_iniciado\"}"))
        }
        conexiones.clear()
    }

    fun agregarConexion(conexion: WebSocketSession) {
        conexiones.add(conexion)
    }

    fun agregarUsuario(idUsuario: String) {
        usuarios.add(Pair(idUsuario, true))
    }

}
