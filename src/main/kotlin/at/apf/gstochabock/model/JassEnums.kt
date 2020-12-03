package at.apf.gstochabock.model

enum class CardColor(val value: String) {
    Eichel("E"),
    Laub("L"),
    Herz("H"),
    Schell("S")
}

enum class CardValue(val value: String) {
    Sechs("6"),
    Sieben("7"),
    Acht("8"),
    Nell("9"),
    Zehn("X"),
    Bauer("U"),
    Ober("O"),
    Koenig("K"),
    Ass("A")
}

enum class WeisRank(val value: String, val points: Int) {
    Drei("3", 20),
    Vier("4", 50),
    Quartett("Q", 100),
    Fuenf("5", 100),
    Sechs("6", 120),
    Sieben("7", 140),
    Acht("8", 160),
    Neuen("9", 180)
    ;

    companion object {
        fun fromValue(v: Int): WeisRank {
            return values().find { it.value == v.toString() }!!
        }
    }
}