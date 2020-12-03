package at.apf.gstochabock.gamelogic

import at.apf.gstochabock.dto.Trumpf
import at.apf.gstochabock.model.*
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class BaseJassLogic : JassLogic {

    private val rnd = Random(System.currentTimeMillis())

    private val trumpfRanking: List<CardValue> = listOf(CardValue.Sechs, CardValue.Sieben, CardValue.Acht, CardValue.Zehn,
            CardValue.Ober, CardValue.Koenig, CardValue.Ass, CardValue.Nell, CardValue.Bauer)

    override fun trumpfRanking(cardValue: CardValue): Int  {
        return trumpfRanking.indexOf(cardValue)
    }

    override fun assignCards(): List<List<Card>> {
        val allCards: MutableList<Card> = mutableListOf()
        for (col in CardColor.values()) {
            for (v in CardValue.values()) {
                allCards.add(Card(col, v))
            }
        }

        val players: MutableList<MutableList<Card>> = mutableListOf()
        for (i in 1..amountPlayers()) {
            val cards: MutableList<Card> = mutableListOf()
            for (j in 1..amountCards()) {
                val r = rnd.nextInt(allCards.size)
                cards.add(allCards[r])
                allCards.removeAt(r)
            }
        }

        return players
    }


    /**
     * Returns a sorted List of the cards.
     */
    override fun sort(cards: List<Card>) : List<Card> {
        return cards.sortedBy { it.hashCode() }
    }

    /**
     * Sums up the points for the given Weis.
     */
    override fun calcWeissPoints(weises: List<Weis>): Int {
        return weises.sumBy {
            if (it.rank === WeisRank.Quartett) {
                return when (it.value) {
                    CardValue.Bauer -> 200
                    CardValue.Nell -> 150
                    else -> 100
                }
            }
            return it.rank.points
        }
    }

    /**
     * Analyzes the cards and returns a list of the found Weises.
     */
    override fun cardsToWeis(cards: List<Card>) : List<Weis> {

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
        for (v in CardValue.values()) {
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

    private fun weisToCards(weis: Weis): List<Card> {
        val cards: MutableList<Card> = mutableListOf()

        if (weis.rank === WeisRank.Quartett) {
            for (c in CardColor.values()) {
                cards.add(Card(c, weis.value))
            }
        } else {
            for (i in (weis.rank.value.toInt() - 1).rangeTo(0)) {
                val v: CardValue = CardValue.values().find { it.ordinal === weis.value.ordinal - i }!!
                cards.add(Card(weis.color!!, v))
            }
        }

        return cards
    }

    override fun weisToCards(weises: List<Weis>): List<Card> {
        val cards: HashSet<Card> = HashSet()
        weises.forEach { cards.addAll(weisToCards(it)) }
        return cards.toList()
    }

    /**
     * Returns true if there are Stoecke in the cards, otherwise false.
     */
    override fun hasStoecke(cards: List<Card>, trumpf: Trumpf): Boolean {
        val trumpfCol = CardColor.values().find { trumpf.equals(it) }
        if (trumpfCol !== null) {
            return cards.contains(Card(trumpfCol, CardValue.Ober))
                    && cards.contains(Card(trumpfCol, CardValue.Koenig))
        }
        return false
    }

    /**
     * Compare function for Weis with the needed trumpf.
     */
    override fun compareWeis(weisA: Weis, weisB: Weis, trumpf: Trumpf): Int {
        if (weisA.rank !== weisB.rank) {
            return weisA.rank.ordinal - weisB.rank.ordinal
        }
        if (weisA.value !== weisB.value) {
            return weisA.value.ordinal - weisB.value.ordinal
        }
        return if (trumpf.equals(weisA.color)) 1 else if (trumpf.equals(weisB.color)) -1 else 0
    }

    /**
     * Compare function for Weises wit hthe needed trumpf.
     */
    override fun compareWeis(weisA: List<Weis>, weisB: List<Weis>, trumpf: Trumpf): Int {
        val bestA = weisA.maxBy { it.hashCode() }
        val bestB = weisB.maxBy { it.hashCode() }

        if (bestA === null && bestB === null) {
            return 0
        }
        if (bestA === null) {
            return -1
        }
        if (bestB === null) {
            return 1
        }

        return compareWeis(bestA, bestB, trumpf)
    }

    /**
     * Checks if card is allowed to play in the given setting of hand cards, round cards and the trumpf of the game.
     */
    override fun cardAllowed(round: List<Card>, card: Card, hand: List<Card>, trumpf: Trumpf): Boolean {
        if (!hand.contains(card)) {
            return false
        }
        if (round.isEmpty()) {
            return true
        }
        if (round[0].color === card.color) {
            return true
        }


        // from here on the card has a different color than the first played one of the round
        val trumpfCol = CardColor.values().find { trumpf.equals(it) }

        // check if card is trumpf
        if (card.color === trumpfCol) {
            val bestPlayedTrumpfCard: Card? = round.filter { it.color === trumpfCol }.maxBy { trumpfRanking(it.value) }
            if (bestPlayedTrumpfCard === null) {
                return true
            }

            // check for untertrumpfing
            if (trumpfRanking(bestPlayedTrumpfCard.value) < trumpfRanking(card.value)) {
                return true
            }

            // if only trumpfs are left on the hand and  all hand cards are lower than the best played
            if (hand.all { trumpf.equals(it.color)
                            && trumpfRanking(it.value) < trumpfRanking(bestPlayedTrumpfCard.value) }) {
                return true
            }

            return false
        }

        // if its a trumpf opening, and the Bauer is the only trumpf on hand, its ok if an other card is palyed
        if (round[0].color === trumpfCol) {
            if (hand.contains(Card(trumpfCol, CardValue.Bauer)) && hand.count { it.color === trumpfCol } === 1) {
                return true
            }
        }

        // no correct color and not a trmpf -> no cards with correct colors are left on the hand
        return hand.none { it.color === round[0].color }
    }

    /**
     * Calculates the winner of the given round and the game trumpf.
     */
    override fun roundWinner(round: List<Card>, trumpf: Trumpf): Int {
        var best = 0
        for (i in 1..3) {
            if (round[i].color == round[best].color && !trumpf.equals(round[best].color)) {
                // has played correct color and its not a trumpf

                if (trumpf !== Trumpf.Geiss && trumpf !== Trumpf.KulmiUnten) {
                    // high card wins
                    if (round[i].value > round[best].value) {
                        best = i
                    }
                } else {
                    // low card wins
                    if (round[i].value < round[best].value) {
                        best = i
                    }
                }
            } else if (trumpf.equals(round[i].color)) {
                // best is not a trumpf or i is a better trumpf
                if (!trumpf.equals(round[best].color) ||
                        trumpfRanking(round[i].value) > trumpfRanking(round[best].value)) {
                    best = i
                }
            }
        }
        return best
    }

    /**
     * Calculates the sum of the points of the played round with the given game trumpf.
     */
    override fun calcPoints(round: List<Card>, trumpf: Trumpf): Int {
        return round.sumBy {
            if (trumpf.equals(it.color)) {
                if (it.value === CardValue.Bauer) {
                    return 20
                }
                if (it.value === CardValue.Nell) {
                    return 14
                }
            }
            if (trumpf >= Trumpf.Geiss && it.value === CardValue.Acht) {
                return 8
            }
            return when(it.value) {
                CardValue.Zehn -> 10
                CardValue.Bauer -> 2
                CardValue.Ober -> 3
                CardValue.Koenig -> 4
                CardValue.Ass -> 11
                else -> 0
            }
        }
    }

}