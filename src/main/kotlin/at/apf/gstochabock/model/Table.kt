package at.apf.gstochabock.model

import at.apf.gstochabock.dto.Trumpf
import at.apf.gstochabock.gamelogic.JassLogic

data class Table(var id: String, var password: String?, var points: MutablePair<Int, Int>, var weisPoints: MutablePair<Int, Int>,
                 var currentMove: Int?, var trumpf: Trumpf?, var round: MutableList<Card>, var lastRound: RoundHistory?,
                 var matschable: Boolean?, var players: MutableList<Player>, var history: Table?, val logic: JassLogic,
                 var state: TableState = TableState.PENDING)

data class RoundHistory(var startPosition: Int, var cards: List<Card>, var winnerPosition: Int)

enum class TableState {
    PENDING, // Waiting for players
    TRUMPF, // Select the trumpf
    PLAYING, // Playing
    FINISHED // All cards are played, points are calculated
}