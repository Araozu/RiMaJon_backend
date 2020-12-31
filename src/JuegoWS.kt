package dev.araozu

import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

val gson = Gson()

data class DatosDescarte(val idJuego: String, val idUsuario: String, val carta: Int)
data class DatosIgnorarOportunidad(val idJuego: String, val idUsuario: String)
data class DatosLlamarSeq(
    val idJuego: String,
    val idUsuario: String,
    val cartaDescartada: Int,
    val combinacion: Pair<Int, Int>
)

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
                    "descarte" -> {
                        val datos = gson.fromJson(sol.datos, DatosDescarte::class.java)
                        GestorJuegos.manejarDescarte(datos.idJuego, datos.idUsuario, datos.carta)
                    }
                    "ignorar_oportunidad" -> {
                        val datos = gson.fromJson(sol.datos, DatosIgnorarOportunidad::class.java)
                        GestorJuegos.manejarIgnorarOportunidad(datos.idJuego, datos.idUsuario)
                    }
                    "llamar_seq", "llamar_tri" -> {
                        val datos = gson.fromJson(sol.datos, DatosLlamarSeq::class.java)
                        GestorJuegos.manejarLlamarSeq(
                            datos.idJuego,
                            datos.idUsuario,
                            datos.cartaDescartada,
                            datos.combinacion
                        )
                    }
                    "llamar_ron" -> {
                        val datos = gson.fromJson(sol.datos, DatosIgnorarOportunidad::class.java)
                        GestorJuegos.manejarRon(datos.idJuego, datos.idUsuario)
                    }
                    "llamar_tsumo" -> {
                        TODO("Tsumo no implementado")
                    }
                }
            }
            else -> {
                System.err.println("Tipo de dato enviado al socket no admitido")
            }
        }

    }

}
