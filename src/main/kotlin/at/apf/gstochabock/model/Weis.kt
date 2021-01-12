package at.apf.gstochabock.model

class Weis  {

    val rank: WeisRank
    val color: CardColor?
    val value: CardValue

    constructor(rank: WeisRank, value: CardValue, color: CardColor? = null) {
        this.rank = rank
        this.color = color
        this.value = value
    }

    constructor(card: String) {
        rank = WeisRank.values().find { it.value == card[0].toString() }!!
        color = if (card.length === 3)   CardColor.values().find { it.value == card[1].toString() }!! else null
        value = CardValue.values().find { it.value == (if (card.length === 3) card[2].toString() else card[1]).toString() }!!
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Weis) {
            return false
        }
        return rank === other.rank && value === other.value
    }

    override fun hashCode(): Int {
        return rank.points + value.ordinal
    }

    override fun toString(): String {
        return rank.value + (if (color !== null) color.value else "") + value.value
    }

}