package dev.araozu

object GestorUsuarios {

    private val usuarios: HashMap<String, String> = HashMap()

    private val letras = arrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )

    private fun generarId(): String {
        var str = ""
        for (i in 0 until 15) {
            str += letras[(Math.random() * letras.size).toInt()]
        }
        return str
    }

    fun crearUsuario(nombreUsuario: String): String {
        var idUsuario = generarId()
        while (usuarios.containsKey(idUsuario)) {
            idUsuario = generarId()
        }
        usuarios[idUsuario] = nombreUsuario
        return idUsuario
    }

    fun validarUsuario(idUsuario: String): String? {
        return usuarios[idUsuario]
    }

    fun obtenerNombreUsuario(idUsuario: String): String {
        return usuarios[idUsuario]!!
    }

}
