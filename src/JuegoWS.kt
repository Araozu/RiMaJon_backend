package dev.araozu

import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

val gson = Gson()

fun Routing.juegows() {

    webSocket("/juego") {

        for (frame in incoming) when (frame) {
            is Frame.Text -> {
                val sol = gson.fromJson(frame.readText(), Conexion::class.java)
                when (sol.operacion) {
                    "conectar" -> {
                        val datos = gson.fromJson(sol.datos, ConexionNueva::class.java)
                        GestorJuegos.conectarASala(datos.idJuego, datos.idUsuario, this)
                    }
                }
            }
        }

    }

}
