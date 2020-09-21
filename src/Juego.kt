package dev.araozu

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

data class InfoJuego(val id: String)
data class DataCrearJuego(val idUsuario: String)

fun Routing.crearJuego() {

    post("/partida") {
        val dataUsuario = call.receive<DataCrearJuego>()

        var sigId = GestorJuegos.generarId()
        while (GestorJuegos.juegos.containsKey(sigId)) {
            sigId = GestorJuegos.generarId()
        }

        val juego = Juego(arrayListOf())
        GestorJuegos.juegos[sigId] = juego

        call.respondText("{\"id\": \"$sigId\"}", contentType = ContentType.Application.Json)
    }

    post("/partida-join") {
        val infoJuego = call.receive<InfoJuego>()
        if (infoJuego.id.length != 6) {
            call.respondText("{\"error\": \"ID invalido.\"}", contentType = ContentType.Application.Json)
        }

        if (GestorJuegos.juegos.containsKey(infoJuego.id)) {
            call.respondText("{\"ok\": true}", contentType = ContentType.Application.Json)
        } else {
            call.respondText("{\"error\": \"El juego no existe\"}", contentType = ContentType.Application.Json)
        }
    }

}
