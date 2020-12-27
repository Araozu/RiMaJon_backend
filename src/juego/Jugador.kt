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

    fun inicializarCartas(cartas: ArrayList<Int>) {
        mano.cartas = cartas
    }

    fun inicializarDragon(dragon: Dragon) {
        mano.dragon = dragon
    }

    abstract fun actualizarConexion(ws: WebSocketSession)

    abstract suspend fun enviarDatos(datos: DatosJuego)

    fun recibirCarta(carta: Int) {
        mano.sigCarta = carta
    }

    /**
     * Intenta descartar una carta de la mano y devuelve si dicho descarte
     * brinda oportunidades a otros jugadores
     * @param cartaDescartada La carta a remover de la mano
     * @return La cantidad de oportunidades
     */
    fun descartarCarta(cartaDescartada: Int): Int {
        val cartaFueDescartada = mano.descartarCarta(cartaDescartada)

        if (!cartaFueDescartada) return -1

        var oportunidadesRestantes = 0
        var posicionJugadorActual = -1
        for ((i, jugador) in juego.jugadores.withIndex()) {
            if (this === jugador) {
                posicionJugadorActual = i
                continue
            }

            var hayOportunidad = false
            val mano = jugador.mano

            // Verificar seq en jugador a la derecha
            if ((posicionJugadorActual + 1) % 4 == i) {
                val oportunidadSeq = OportunidadSeq.verificar(cartaDescartada, mano.cartas)
                if (oportunidadSeq != null) {
                    hayOportunidad = true
                    mano.oportunidades.add(oportunidadSeq)
                }
            }

            // Oportunidades tri
            val oportunidadTri = OportunidadTri.verificar(cartaDescartada, mano.cartas)
            if (oportunidadTri != null) {
                hayOportunidad = true
                mano.oportunidades.add(oportunidadTri)
            }

            // Oportunidades win (ron)
            val oportunidadWin = OportunidadWin.verificar(cartaDescartada, mano.cartas, mano.cartasReveladas)
            if (oportunidadWin != null) {
                hayOportunidad = true
                mano.oportunidades.add(oportunidadWin)
            }

            if (hayOportunidad) oportunidadesRestantes++
        }

        return oportunidadesRestantes
    }

    /**
     * Limpia las oportunidades del jugador y verifica si algun otro jugador tiene alguna oportunidad
     * @return Si otro jugador tiene alguna oportunidad
     */
    fun ignorarOportunidades(): Boolean {
        mano.oportunidades.clear()

        return null != juego.jugadores.find {
            it !== this && it.mano.oportunidades.size > 0
        }
    }

    abstract fun verificarTsumo()

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

    override fun verificarTsumo() {
        System.err.println("Tsumo no implementado D:")
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

    override fun verificarTsumo() {
        System.err.println("Tsumo no implementado D:")
    }

}
