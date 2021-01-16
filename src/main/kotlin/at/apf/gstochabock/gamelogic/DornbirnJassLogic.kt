package at.apf.gstochabock.gamelogic

import at.apf.gstochabock.model.*

/**
 * Differences:
 *   - never allowe quartett for 6, 7, 8
 *   - always higher card is prefered for weising, also in geis trumpf
 *
 */
class DornbirnJassLogic : BaseJassLogic() {

    override fun serializationCode(): String = "dornbirn"

    override fun cardsToWeis(cards: List<Card>, trumpf: Trumpf): List<Weis> {
        val cards2: MutableList<Card> = sort(cards).toMutableList()
        val weis: MutableList<Weis> = mutableListOf()

        // Prefer QU and Q9
        if (cards2.count { it.value === CardValue.Bauer } === 4) {
            weis.add(Weis(WeisRank.Quartett, CardValue.Bauer))
            cards2.removeIf { it.value === CardValue.Bauer }
        }
        if (cards2.count { it.value === CardValue.Nell } === 4) {
            weis.add(Weis(WeisRank.Quartett, CardValue.Nell))
            cards2.removeIf { it.value === CardValue.Nell }
        }

        // WeisRank >= 5
        weis.addAll(cardsToStraightWeis(cards2, 5))

        // Quartett
        for (v in CardValue.values().filter { it > CardValue.Acht }) {
            if (cards2.count { it.value === v } === 4) {
                weis.add(Weis(WeisRank.Quartett, v))
                cards2.removeIf { it.value === v }
            }
        }

        //WeisRank == 3 || 4
        weis.addAll(cardsToStraightWeis(cards2, 3))

        return weis
    }

    /**
     * Searchs for all straight weises greater or equal to the given rank and removes the used cards from the cards list.
     */
    private fun cardsToStraightWeis(cards: MutableList<Card>, rank: Int): List<Weis> {
        val weis: MutableList<Weis> = mutableListOf()
        for (col in CardColor.values()) {
            val cardsCol = cards.filter { it.color === col }.toMutableList()
            var i = 0
            while (i < cardsCol.size) {
                var straight = 1
                while (i + straight < cardsCol.size) {
                    if (cardsCol[i + straight - 1].value.ordinal + 1 !== cardsCol[i + straight].value.ordinal) {
                        break
                    }
                    straight++
                }
                if (straight >= rank) {
                    weis.add(Weis(WeisRank.fromValue(straight), cardsCol[i + straight - 1].value, col))
                    val removeCards = cardsCol.subList(i, i + straight - 1)
                    cards.removeAll(removeCards)
                    cardsCol.removeAll(removeCards)
                }
                i++
            }
        }
        return weis
    }

    override fun compareWeis(weisA: Weis, weisB: Weis, trumpf: Trumpf): Int {
        if (weisA.rank !== weisB.rank) {
            return weisA.rank.ordinal - weisB.rank.ordinal
        }
        if (weisA.value !== weisB.value) {
            return weisA.value.ordinal - weisB.value.ordinal
        }
        return if (trumpf.equals(weisA.color)) 1 else if (trumpf.equals(weisB.color)) -1 else 0
    }

}