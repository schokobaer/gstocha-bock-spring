package at.apf.gstochabock.mapper

import at.apf.gstochabock.dto.*
import at.apf.gstochabock.model.Stoeckability
import at.apf.gstochabock.model.Table
import at.apf.gstochabock.model.TableState
import at.apf.gstochabock.model.Trumpf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TableMapper {

    @Autowired
    lateinit var playerMapper: PlayerMapper

    fun toTableDto(t: Table): TableDto {
        return TableDto(t.id, t.password !== null, t.players.map { playerMapper.toTablePlayerDto(it) }, t.randomizePlayerOrder ?: false)
    }

    fun toGameDto(t: Table, playerid: String): GameDto {
        var roundHistory: MutableList<GameRoundDto> = mutableListOf()
        var weisPoints: MutableList<WeisPoints>? = null
        val players: MutableList<GamePlayerDto> = mutableListOf()

        // not first round
        if (t.roundHistory.isNotEmpty()) {
            roundHistory.add(
                    GameRoundDto(
                        t.roundHistory.last().startPosition,
                        t.roundHistory.last().cards.map { it.toString() },
                        t.roundHistory.last().teamIndex
                    )
            )
        }

        if (t.state === TableState.FINISHED) {

            // weisPoints
            weisPoints = mutableListOf()
            for (i in t.weisPoints.indices) {
                var hasStoecke = false
                for (j in t.players.indices) {
                    if (j % t.logic.amountTeams() === i && t.players[j].stoecke === Stoeckability.Called) {
                        hasStoecke = true
                    }
                }
                weisPoints.add(WeisPoints(t.weisPoints[i], hasStoecke))
            }

            // round history
            roundHistory.clear()
            roundHistory.addAll(t.roundHistory.map { GameRoundDto(it.startPosition, it.cards.map { c -> c.toString() }, it.teamIndex) })
        }

        // players
        val showWeiss = t.players.all { it.cards.size === t.logic.amountCards() - 1 }
        val playersCopy = t.players.toMutableList()
        val playerPos = t.players.find { it.playerid == playerid }!!.position
        // sort the players copy, so the playerPos is the first
        playersCopy.sortBy { (it.position + (t.logic.amountPlayers() - playerPos)) % (t.logic.amountPlayers()) }
        playersCopy.forEach {
            val weis: List<String>? = if (showWeiss) it.weises.map { w -> w.toString() } else null
            var weisCall: String? = null
            if (it.cards.size === t.logic.amountCards() - 1) {
                val weisesSorted = it.weises.toMutableList()
                weisesSorted.sortBy { w -> w.hashCode() }
                if (weisesSorted.size > 0) {
                    weisCall = weisesSorted.last().rank.value
                }
            }
            players.add(
                    GamePlayerDto(
                            it.name,
                            it.position,
                            weis,
                            weisCall,
                            it.stoecke === Stoeckability.Called
                    )
            )
        }

        val player = t.players.find { it.playerid == playerid }!!

        val undoable = t.history !== null && (
                        t.state === TableState.TRUMPF
                        || (t.state === TableState.PLAYING && t.players.all { it.cards.size === t.logic.amountCards() })
                )

        return GameDto(
                t.currentMove,
                t.trumpf?.value,
                if (t.state === TableState.FINISHED) t.points else null,
                weisPoints,
                t.round.map { it.toString() },
                roundHistory,
                undoable,
                players,
                player.cards.map { it.toString() },
                player.stoecke.name,
                if (t.writer !== null) WriterDto(
                        t.writer.serializationCode(),
                        t.writer.export(),
                        t.writer.currentTeam,
                        t.writer.currentTrumpf?.value
                ) else null,
                t.puck?.position,
                t.state.name
        )
    }

}