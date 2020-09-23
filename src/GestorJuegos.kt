package dev.araozu

import io.ktor.http.cio.websocket.*

object GestorJuegos {

    private val todasCartas = arrayOf(
        34, 34, 2, 2, 35, 35, 3, 3, 36, 36, 4, 4, 37, 37, 5, 5, 38, 38, 6, 6, 39, 39, 7, 7, 40, 40, 8, 8, 41,
        41, 9, 9, 42, 42, 10, 10, 43, 43, 11, 11, 44, 44, 12, 12, 45, 45, 13, 13, 46, 46, 14, 14, 47, 47, 15, 15, 48,
        48, 16, 16, 49, 49, 17, 17, 50, 50, 18, 18, 51, 51, 19, 19, 52, 52, 20, 20, 53, 53, 21, 21, 64, 64, 64, 64, 96,
        96, 96, 96, 128, 128, 128, 128, 160, 160, 160, 160, 192, 224, 256, 192, 224, 256, 192, 224, 256, 192, 224, 256
    )

    private val letras = arrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )

    val juegos = HashMap<String, Juego>()

    fun generarId(): String {
        var str = ""
        for (i in 0 until 6) {
            str += letras[(Math.random() * letras.size).toInt()]
        }
        return str
    }

    fun generarCartas(): Array<Int> {
        val cartasR = todasCartas.clone()

        var max = cartasR.size - 1
        while (max > 0) {
            val indice = (Math.random() * max).toInt()
            val elemR = cartasR[indice]
            cartasR[indice] = cartasR[max]
            cartasR[max] = elemR
            max -= 1
        }

        return cartasR
    }

    private suspend fun broadcast(juego: Juego, mensaje: String) {
        juego.conexiones.forEach { socket ->
            socket.send(Frame.Text(mensaje))
        }
    }

    suspend fun conectarASala(idJuego: String, idUsuario: String, ws: WebSocketSession) {
        val juego = juegos[idJuego]!!
        val nombreUsuario = GestorUsuarios.obtenerNombreUsuario(idUsuario)
        val mensaje = "{\"operacion\": \"usuario_conectado\", \"idUsuario\": \"$idUsuario\", \"nombreUsuario\": \"${nombreUsuario}\"}"

        broadcast(juego, mensaje)

        var str = "["
        for ((idUsuarioAct, _) in juego.usuarios) {
            val nombreUsuarioAct = GestorUsuarios.obtenerNombreUsuario(idUsuarioAct)
            if (str.length != 1) str += ","
            str += "{\"idUsuario\": \"$idUsuarioAct\", \"nombreUsuario\": \"$nombreUsuarioAct\"}"
        }
        str += "]"
        juego.agregarConexion(ws)
        juego.agregarUsuario(idUsuario)
        ws.send(Frame.Text("{\"operacion\": \"conexion_exitosa\", \"jugadores\": $str}"))
    }

}
