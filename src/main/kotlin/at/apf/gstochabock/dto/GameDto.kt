package at.apf.gstochabock.dto

data class GameDto(val currentMove: Int?, val trumpf: String?, val points: List<Int>?,
                   val weiPoints: List<WeisPoints>?, val round: List<String>, val lastRound: LastRound?,
                   val undoable: Boolean, val players: List<GamePlayerDto>, val cards: List<String>)

data class WeisPoints(val points: Int, val stoecke: Boolean)
data class LastRound(val startPosition: Int, val cards: List<String>)