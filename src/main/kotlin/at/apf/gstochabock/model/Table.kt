package at.apf.gstochabock.model

import at.apf.gstochabock.dto.Trumpf

data class Table(var id: String, var password: String?, var points: Pair<Int, Int>, var weisPoints: Pair<Int, Int>,
                 var currentMove: Int?, var trumpf: Trumpf?, var round: List<Card>, var lastRound: RoundHistory?,
                 var matschable: Boolean?, var players: List<Player>, var history: String?)

data class RoundHistory(var startPosition: Int, var cards: List<Card>, var winnerPosition: Int)