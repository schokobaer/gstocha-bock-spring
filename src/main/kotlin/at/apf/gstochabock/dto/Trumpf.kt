package at.apf.gstochabock.dto

import at.apf.gstochabock.model.CardColor

enum class Trumpf(val value: String) {
    Eichel("E"),
    Laub("L"),
    Herz("H"),
    Schell("S"),
    Geiss("G"),
    Bock("B"),
    KulmiUnten("KU"),
    KulmiOben("KO")
    ;

    fun equals(color: CardColor?): Boolean {
        if (color === null) {
            return false
        }
        return color.value === value
    }
}