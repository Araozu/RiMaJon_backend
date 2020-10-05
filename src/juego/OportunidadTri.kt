package dev.araozu.juego

data class OportunidadTri(override val cartaDescartada: Int, override val cartasOportunidad: ArrayList<Int>) :
    Oportunidad
