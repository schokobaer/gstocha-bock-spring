package at.apf.gstochabock.dto

data class GameDto(val currentMove: Int?, val trumpf: Trumpf?, val points: Pair<Int, Int>?,
                   val weiPoints: Pair<WeisPoints, WeisPoints>?, val round: List<String>, val lastRound: LastRound?,
                   val undoable: Boolean, val players: List<PlayerDto>)

data class WeisPoints(val points: Int, val stoecke: Boolean)
data class LastRound(val startPosition: Int, val cards: List<String>)