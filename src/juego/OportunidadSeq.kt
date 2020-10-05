package dev.araozu.juego

data class OportunidadSeq(override val cartaDescartada: Int, override val cartasOportunidad: ArrayList<Int>) :
    Oportunidad
