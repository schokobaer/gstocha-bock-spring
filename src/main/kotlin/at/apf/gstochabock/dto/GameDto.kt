package at.apf.gstochabock.dto

import at.apf.gstochabock.model.Stoeckability

data class GameDto(
        val currentMove: Int?,
        val trumpf: String?,
        val points: List<Int>?,
        val weisPoints: List<WeisPoints>?,
        val round: List<String>,
        val roundHistory: MutableList<GameRoundDto>,
        val undoable: Boolean,
        val players: List<GamePlayerDto>,
        val cards: List<String>,
        val stoecke: String,
        val puck: Int?,
        val state: String
)

data class WeisPoints(
        val points: Int,
        val stoecke: Boolean
)

data class GameRoundDto(
        val startPosition: Int,
        val cards: List<String>,
        val teamIndex: Int
)