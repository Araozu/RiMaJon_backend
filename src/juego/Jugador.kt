package dev.araozu.juego

import dev.araozu.gson
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class Jugador(val juego: Juego, val idUsuario: String) {

    abstract val isActive: Boolean
    abstract suspend fun send(v: Frame.Text)
    val mano = Mano()

    fun inicializarMano(cartas: ArrayList<Int>) {
        mano.cartas = cartas
    }

    abstract fun actualizarConexion(ws: WebSocketSession)

    abstract suspend fun enviarDatos(datos: DatosJuego)

}

class JugadorHumano(juego: Juego, idUsuario: String, private var ws: WebSocketSession) : Jugador(juego, idUsuario) {

    override val isActive: Boolean
        get() = ws.isActive

    override suspend fun send(v: Frame.Text) {
        ws.send(v)
    }

    override fun actualizarConexion(ws: WebSocketSession) {
        this.ws = ws
    }

    override suspend fun enviarDatos(datos: DatosJuego) {
        val manos = HashMap<String, Mano>()

        juego.jugadores.forEach {
            if (it === this) {
                manos[idUsuario] = this.mano
            } else {
                manos[it.idUsuario] = it.mano
            }
        }

        val datosJuego = datos.copy(
            manos = manos
        )
        ws.send(Frame.Text("{\"operacion\": \"actualizar_datos\", \"datos\": ${gson.toJson(datosJuego)}}"))
    }

}

class JugadorBot(juego: Juego, idUsuario: String) : Jugador(juego, idUsuario) {

    override val isActive: Boolean = true

    override suspend fun send(v: Frame.Text) {
        println("Bot pensando...")
        TODO("Bot no implementado D:")
    }

    override fun actualizarConexion(ws: WebSocketSession) {}

    override suspend fun enviarDatos(datos: DatosJuego) {
        // Si es turno del bot
        GlobalScope.launch {
            delay(1000)
        }

        // Si el bot tiene una oportunidad


    }

}
