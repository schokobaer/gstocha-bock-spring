package at.apf.gstochabock.gamelogic.writer

import at.apf.gstochabock.model.Trumpf

enum class WriterTrumpf(val value: String) {
    Eichel("E"),
    Laub("L"),
    Herz("H"),
    Schell("S"),
    Geiss("G"),
    Bock("B"),
    Sieben("7"),
    Acht("8"),
    Kulmi("K"),
    FuenfVier("F");


    companion object {
        fun fromTrumpf(trumpf: Trumpf, joker: String?): WriterTrumpf {
            return when {
                "7".equals(joker) -> Sieben
                "8".equals(joker) -> Acht
                trumpf.value.startsWith("K") -> Kulmi
                else -> values().find { it.value.equals(trumpf.value) }!!
            }
        }
    }
}