package dev.araozu

import dev.araozu.juego.Juego
import io.ktor.http.cio.websocket.*

object GestorJuegos {

    private val todasCartas = arrayOf(
        2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17,
        18, 18, 19, 19, 20, 20, 21, 21, 34, 34, 35, 35, 36, 36, 37, 37, 38, 38, 39, 39, 40, 40, 41, 41, 42, 42, 43, 43,
        44, 44, 45, 45, 46, 46, 47, 47, 48, 48, 49, 49, 50, 50, 51, 51, 52, 52, 53, 53, 64, 64, 64, 64, 96, 96, 96, 96,
        128, 128, 128, 128, 160, 160, 160, 160, 192, 192, 192, 192, 224, 224, 224, 224, 256, 256, 256, 256
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
        juego.jugadores.forEach {
            if (it.isActive) it.send(Frame.Text(mensaje))
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
        juego.agregarConexion(idUsuario, ws)
        juego.agregarUsuario(idUsuario)
        ws.send(Frame.Text("{\"operacion\": \"conexion_exitosa\", \"jugadores\": $str}"))
    }

    suspend fun iniciarJuego(idJuego: String, ws: WebSocketSession) {
        val juego = juegos[idJuego]
        if (juego != null) {
            juego.iniciarJuego()
        } else {
            ws.send(Frame.Text("{\"operacion\": \"error\", \"razon\": \"Juego invalido\"}"))
        }
    }

    suspend fun manejarDescarte(idJuego: String, idUsuario: String, carta: Int) {
        val juego = juegos[idJuego]!!
        juego.manejarDescarte(idUsuario, carta)
    }

    suspend fun manejarIgnorarOportunidad(idJuego: String, idUsuario: String) {
        val juego = juegos[idJuego]!!
        juego.ignorarOportunidades(idUsuario)
    }

    suspend fun manejarLlamarSeq(idJuego: String, idUsuario: String, cartaDescartada: Int, combinacion: Pair<Int, Int>) {
        val juego = juegos[idJuego]!!
        juego.manejarSeqTri(idUsuario, cartaDescartada, combinacion)
    }

}
