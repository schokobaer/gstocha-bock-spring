package at.apf.gstochabock.mapper

import at.apf.gstochabock.dto.*
import at.apf.gstochabock.model.Table
import at.apf.gstochabock.model.TableState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TableMapper {

    @Autowired
    lateinit var playerMapper: PlayerMapper

    fun toTableDto(t: Table): TableDto {
        return TableDto(t.id, t.password !== null, t.players.map { playerMapper.toTablePlayerDto(it) })
    }

    fun toGameDto(t: Table, playerid: String): GameDto {
        var lastRound: LastRound? = null
        var weisPoints: MutableList<WeisPoints>? = null
        val players: MutableList<GamePlayerDto> = mutableListOf()

        // last round
        if (t.lastRound !== null) {
            lastRound = LastRound(t.lastRound!!.startPosition, t.lastRound!!.cards.map { it.toString() })
        }

        // weisPoints
        if (t.state === TableState.FINISHED) {
            weisPoints = mutableListOf()
            for (i in t.weisPoints.indices) {
                var hasStoecke = false
                for (j in t.players.indices) {
                    if (j % t.logic.amountTeams() === i && t.players[j].stoeckeable === true) {
                        hasStoecke = true
                    }
                }
                weisPoints.add(WeisPoints(t.weisPoints[i], hasStoecke))
            }
        }

        // players
        val showWeiss = t.players.all { it.cards.size === t.logic.amountCards() - 1 }
        val playersCopy = t.players.toMutableList()
        val playerPos = t.players.find { it.playerid == playerid }!!.position
        // sort the players copy, so the playerPos is the first
        playersCopy.sortBy { (it.position + (t.logic.amountPlayers() - playerPos)) % (t.logic.amountPlayers()) }
        playersCopy.forEach {
            val weis: List<String>? = if (showWeiss) it.weises.map { w -> w.toString() } else null
            players.add(GamePlayerDto(it.name, it.position, weis))
        }

        return GameDto(
                t.currentMove,
                t.trumpf?.value,
                if (t.state === TableState.FINISHED) t.points else null,
                weisPoints,
                t.round.map { it.toString() },
                lastRound,
                t.history !== null,
                players,
                t.players.find { it.playerid == playerid }!!.cards.map { it.toString() },
                t.state.name
        )
    }

}