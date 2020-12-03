package at.apf.gstochabock.gamelogic

import at.apf.gstochabock.dto.Trumpf
import at.apf.gstochabock.model.Card
import at.apf.gstochabock.model.CardValue
import at.apf.gstochabock.model.Weis

interface JassLogic {

    fun allowedTrumpfs() : List<Trumpf> = Trumpf.values().toList()

    fun amountPlayers() : Int = 4

    fun amountCards() : Int = 9

    fun trumpfRanking(cardValue: CardValue): Int

    fun assignCards() : List<List<Card>>

    fun sort(cards: List<Card>) : List<Card>

    fun calcWeissPoints(weises: List<Weis>): Int

    fun cardsToWeis(cards: List<Card>) : List<Weis>

    fun weisToCards(weises: List<Weis>): List<Card>

    fun hasStoecke(cards: List<Card>, trumpf: Trumpf): Boolean

    fun compareWeis(weisA: Weis, weisB: Weis, trumpf: Trumpf): Int

    fun compareWeis(weisA: List<Weis>, weisB: List<Weis>, trumpf: Trumpf): Int

    fun cardAllowed(round: List<Card>, card: Card, hand: List<Card>, trumpf: Trumpf): Boolean

    fun roundWinner(round: List<Card>, trumpf: Trumpf): Int

    fun calcPoints(round: List<Card>, trumpf: Trumpf): Int
}