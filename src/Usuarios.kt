package dev.araozu

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

data class Usuario(val nombreUsuario: String)
data class UsuarioValidar(val nombreUsuario: String, val idUsuario: String)

fun Routing.usuarios() {

    post("/usuario/crear") {
        val nombreUsuario = call.receive<Usuario>().nombreUsuario
        val idUsuario = GestorUsuarios.crearUsuario(nombreUsuario)
        call.respondText("{\"id\": \"$idUsuario\"}", contentType = ContentType.Application.Json)
    }

    post("/usuario/validar") {
        val datos = call.receive<UsuarioValidar>()
        val nombreUsuarioValidado = GestorUsuarios.validarUsuario(datos.idUsuario)
        if (nombreUsuarioValidado != null) {
            if (nombreUsuarioValidado == datos.nombreUsuario) {
                call.respondText("{\"estado\": \"ok\"}", contentType = ContentType.Application.Json)
            } else {
                call.respondText(
                    "{\"estado\": \"nombreUsuarioInvalido\", \"nombreUsuario\": \"$nombreUsuarioValidado\"}",
                    contentType = ContentType.Application.Json
                )
            }
        } else {
            call.respondText("{\"estado\": \"idInvalido\"}", contentType = ContentType.Application.Json)
        }
    }

}