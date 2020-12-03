package at.apf.gstochabock.model

class Card {

    val color: CardColor
    val value: CardValue

    constructor(color: CardColor, value: CardValue) {
        this.color = color
        this.value = value
    }

    constructor(card: String) {
        this.color = CardColor.values().find { it.value == card[0].toString() }!!
        this.value = CardValue.values().find { it.value == card[1].toString() }!!
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Card) {
            return false
        }
        return color === other.color && value === other.value
    }

    override fun hashCode(): Int {
        return (color.ordinal * 10) + value.ordinal
    }

    override fun toString(): String {
        return color.value + value.value
    }
}