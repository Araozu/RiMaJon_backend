package dev.araozu.juego

import dev.araozu.gson
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

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

    fun manejarTriSeq(jugadorDescarte: Jugador, cartaARobar: Int, combinacion: Pair<Int, Int>): Boolean {
        // Este jugador es el que roba

        if (!jugadorDescarte.ultimaCartaDescartadaEs(cartaARobar)) {
            System.err.println("Un jugador intento robar una carta no descartada")
            return false
        }

        val (vCarta1, vCarta2) = combinacion

        // El robador no tiene las cartas con las que formar seq
        if (!mano.cartas.contains(vCarta1) || !mano.cartas.contains(vCarta2)) {
            System.err.println("El jugador no tiene las cartas que dice que tiene: $vCarta1, $vCarta2")
            return false
        }

        // Quitar cartas de la mano y moverlas a cartas reveladas
        mano.cartas.remove(vCarta1)
        mano.cartas.remove(vCarta2)
        val grupoAbierto = arrayListOf(cartaARobar, vCarta1, vCarta2)
        grupoAbierto.sort()
        mano.cartasReveladas.add(grupoAbierto)

        jugadorDescarte.eliminarUltimaCartaDescartada()

        return true
    }

    private fun ultimaCartaDescartadaEs(carta: Int): Boolean =
        mano.descartes[mano.descartes.size - 1] == carta

    private fun eliminarUltimaCartaDescartada() {
        mano.descartes.removeAt(mano.descartes.size - 1)
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
                manos[it.idUsuario] = it.mano.obtenerManoPrivada()
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
    private val mutexDescarte = Mutex()
    private val mutexOportunidad = Mutex()

    override suspend fun send(v: Frame.Text) {
        println("Datos enviados a bot, pero ignorados.")
    }

    override fun actualizarConexion(ws: WebSocketSession) {}

    override suspend fun enviarDatos(datos: DatosJuego) {
        println("Bot $idUsuario pensando")

        // Si el bot tiene una carta adicional
        if (mano.sigCarta != -1) {
            // Espera 1s y la descarta
            GlobalScope.launch {
                mutexDescarte.lock()
                delay(1000)
                println("Bot $idUsuario descartando la carta que recibio (${mano.sigCarta})")
                juego.manejarDescarte(idUsuario, mano.sigCarta)
                mutexDescarte.unlock()
            }
        }

        // Si el bot tiene oportunidades
        if (mano.oportunidades.size > 0) {
            // Espera 1s e ignora oportunidades
            GlobalScope.launch {
                mutexOportunidad.lock()
                delay(1000)
                println("Bot $idUsuario ignorando sus oportunidades (${mano.oportunidades.size}")
                juego.ignorarOportunidades(idUsuario)
                mutexOportunidad.unlock()
            }
        }

    }

    override fun verificarTsumo() {
        System.err.println("Tsumo no implementado D:")
    }

}
