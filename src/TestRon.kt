package dev.araozu

/*
    122334
    112233

*/

fun obtSeq(arrl: ArrayList<Int>): ArrayList<ArrayList<Int>> {
    val arrlR = arrayListOf<ArrayList<Int>>()
    var i = 0
    while (i < arrl.size) {
        val primerElem = arrl[i]
        if (arrl.contains(primerElem + 1) && arrl.contains(primerElem + 2)) {
            arrl.remove(primerElem)
            arrl.remove(primerElem + 1)
            arrl.remove(primerElem + 2)
            arrlR.add(arrayListOf(primerElem, primerElem + 1, primerElem + 2))
        } else {
            i++
        }
    }

    return arrlR
}

fun obtSeqInv(arrl: ArrayList<Int>): ArrayList<ArrayList<Int>> {
    val arrlR = arrayListOf<ArrayList<Int>>()
    var i = arrl.size - 1
    while (i >= 0) {
        val primerElem = arrl[i]
        if (arrl.contains(primerElem - 1) && arrl.contains(primerElem - 2)) {
            arrl.remove(primerElem)
            arrl.remove(primerElem - 1)
            arrl.remove(primerElem - 2)
            i -= 3
            arrlR.add(arrayListOf(primerElem, primerElem - 1, primerElem - 2))
        } else {
            i--
        }
    }

    return arrlR
}

fun obtTri(arrl: ArrayList<Int>): ArrayList<ArrayList<Int>> {
    val arrlR = arrayListOf<ArrayList<Int>>()
    var i = 0
    while (i + 2 < arrl.size) {
        val primerElem = arrl[i]
        if (primerElem == arrl[i + 1] && primerElem == arrl[i + 2]) {
            arrl.remove(primerElem)
            arrl.remove(primerElem)
            arrl.remove(primerElem)
            arrlR.add(arrayListOf(primerElem, primerElem, primerElem))
        } else {
            i++
        }
    }
    return arrlR
}

fun obtPar(arrl: ArrayList<Int>): ArrayList<ArrayList<Int>> {
    val arrlR = arrayListOf<ArrayList<Int>>()
    var i = 0
    while (i + 1 < arrl.size) {
        val primerElem = arrl[i]
        if (primerElem == arrl[i + 1]) {
            arrl.remove(primerElem)
            arrl.remove(primerElem)
            arrlR.add(arrayListOf(primerElem, primerElem))
        } else {
            i++
        }
    }
    return arrlR
}

fun main() {
    val cartas  = arrayListOf(2, 2, 2, 3, 3, 4, 4)
    val cartas2 = arrayListOf(2, 2, 2, 3, 3, 4, 4)

    val arrlR = obtSeq(cartas)
    val arrlT = obtTri(cartas)
    val arrlP = obtPar(cartas)

    println("-------------------")
    println(arrlR)
    println(arrlT)
    println(arrlP)
    println(cartas)

    val arrlR2 = obtSeqInv(cartas2)
    val arrlT2 = obtTri(cartas2)
    val arrlP2 = obtPar(cartas2)

    println("-------------------")
    println(arrlR2)
    println(arrlT2)
    println(arrlP2)
    println(cartas2)
}
