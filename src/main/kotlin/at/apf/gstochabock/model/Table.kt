package at.apf.gstochabock.model

import at.apf.gstochabock.gamelogic.JassLogic
import at.apf.gstochabock.gamelogic.writer.GameWriter

data class Table(
        var id: String,
        var password: String?,
        var points: MutableList<Int>,
        var weisPoints: MutableList<Int>,
        var currentMove: Int?,
        var trumpf: Trumpf?,
        var round: MutableList<Card>,
        val roundHistory: MutableList<RoundHistory>,
        var players: MutableList<Player>,
        var history: Table?,
        val created: String,
        val logic: JassLogic,
        val puck: Puck? = null,
        val writer: GameWriter?,
        val randomizePlayerOrder: Boolean? = false,
        var state: TableState = TableState.PENDING

)

data class RoundHistory(
        var startPosition: Int,
        var cards: List<Card>,
        var winnerPosition: Int,
        var teamIndex: Int
)

enum class TableState {
    PENDING, // Waiting for players
    TRUMPF, // Select the trumpf
    PLAYING, // Playing
    FINISHED // All cards are played, points are calculated
}

data class Puck (
        var position: Int,
        var starter: Card?
)

