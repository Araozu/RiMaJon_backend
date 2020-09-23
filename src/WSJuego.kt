package dev.araozu

import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

data class Conexion(val operacion: String, val datos: String)

data class ConexionNueva(val idJuego: String, val idUsuario: String)

fun Routing.wsjuego() {
    val gson = Gson()
    webSocket("/socket") {

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val datos = gson.fromJson(frame.readText(), Conexion::class.java)
                    when (datos.operacion) {
                        "conectar" -> {
                            val datos2 = gson.fromJson(datos.datos, ConexionNueva::class.java)
                            GestorJuegos.conectarASala(datos2.idJuego, datos2.idUsuario, this)
                        }
                        "iniciar" -> {
                            val datos2 = gson.fromJson(datos.datos, ConexionNueva::class.java)
                            GestorJuegos.iniciarJuego(datos2.idJuego, this)
                        }
                    }
                }
            }
        }

    }
}
