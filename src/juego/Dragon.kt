package dev.araozu.juego

enum class Dragon {
    Negro,
    Rojo,
    Verde,
    Azul;

    companion object {
        fun get(pos: Int) =
            when (pos) {
                0 -> Negro
                1 -> Rojo
                2 -> Verde
                3 -> Azul
                else -> throw Error("Dragon incorrecto.")
            }

        fun sigDragon(d: Dragon) =
            when (d) {
                Negro -> Rojo
                Rojo -> Verde
                Verde -> Azul
                Azul -> Negro
            }
    }

}
