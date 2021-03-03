package at.apf.gstochabock.service

import at.apf.gstochabock.gamelogic.writer.BaseWriter
import at.apf.gstochabock.gamelogic.writer.WriterTrumpf
import at.apf.gstochabock.log.GameEventLogger
import at.apf.gstochabock.model.Trumpf
import at.apf.gstochabock.model.*
import at.apf.gstochabock.repo.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*
import java.util.stream.Collectors

@Service
class TableService {

    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var notifyService: WebSocketNotifyService

    @Autowired
    lateinit var logger: GameEventLogger

    fun nextGame(table: Table) {
        table.currentMove = null
        table.points.clear()
        table.weisPoints.clear()
        table.round.clear()
        table.roundHistory.clear()
        table.trumpf = null
        table.state = TableState.TRUMPF

        var i = 0
        while (i < table.logic.amountTeams()) {
            table.points.add(0)
            table.weisPoints.add(0)
            i++
        }

        val playercards: List<List<Card>> = table.logic.assignCards()
        for (i in playercards.indices) {
            table.players[i].cards = table.logic.sort(playercards[i]).toMutableList()
            table.players[i].stoecke = Stoeckability.None
            table.players[i].weises = mutableListOf()
        }

        //table.cardDistribution = playercards.map { it.toMutableList() }.toMutableList()
    }

    /**
     * When a game starts, it searches for the player with the puck card and assigns thats players position.
     * If there is no card, a random is used. If the puck is empty, no puck will be assigned.
     */
    fun organizePuck(table: Table) {
        if (table.puck !== null) {
            if (table.puck.starter !== null) {
                table.puck.position = table.players.find {
                        p -> p.cards.find { it.equals(table.puck.starter) } !== null
                }?.position ?: Random().nextInt(table.logic.amountPlayers())
                logger.info(table.id, " -- ", "puck", "Starter: ${table.puck.position}")
            } else {
                table.puck.position = Random().nextInt(table.logic.amountPlayers())
                logger.info(table.id, " -- ", "puck", "Random order: ${table.puck.position}")
            }
        }
    }

    fun addHistory(table: Table) {
        val t2 = Table(
                table.id,
                table.password,
                table.points.toMutableList(),
                table.weisPoints.toMutableList(),
                table.currentMove,
                table.trumpf,
                table.round.toMutableList(),
                table.roundHistory.toMutableList(),
                table.players.map {
                    Player(
                            it.playerid,
                            it.name,
                            it.position,
                            it.weises.toMutableList(),
                            it.cards.toMutableList(),
                            it.stoecke
                    )
                }.toMutableList(),
                null,
                table.created,
                table.logic,
                if (table.puck !== null) Puck(table.puck.position, table.puck.opens, table.puck.starter) else null,
                table.writer?.clone(),
                table.randomizePlayerOrder ?: false,
                //if (table.state === TableState.TRUMPF) table.players.map { it.cards.toMutableList() }.toMutableList() else null,
                null,
                table.state
        )
        table.history = t2
    }

    private fun getPlayer(table: Table, playerid: String): Player {
        val player = table.players.find { it.playerid == playerid }
        if (player === null) {
            gameRepo.unlock(table.id)
            throw RuntimeException("Player not at table")
        }
        return player
    }

    fun getGameTable(tableid: String): Table {
        return gameRepo.read(tableid)
    }

    fun setTrumpf(tableid: String, playerid: String, trumpf: Trumpf, joker: String?) {
        val table = gameRepo.lockedRead(tableid)
        val player = getPlayer(table, playerid)

        if (table.state !== TableState.TRUMPF) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "setTrumpf", "tried to set trumpf in state ${table.state}")
            throw RuntimeException("Trumpf not allowed")
        }
        if (!table.logic.allowedTrumpfs().contains(trumpf)) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "setTrumpf", "tried to set unallowed trumpf $trumpf")
            throw RuntimeException("Trumpf not allowed")
        }
        if (table.writer !== null) {
            val teamIdx = player.position % table.logic.amountTeams()
            val writerTrumpf = WriterTrumpf.fromTrumpf(trumpf, joker)
            if (!table.writer.openTrumpfs(teamIdx).contains(writerTrumpf)) {
                gameRepo.unlock(tableid)
                logger.warn(tableid, player.name, "setTrumpf", "tried to set unallowed writer trumpf $writerTrumpf")
                throw RuntimeException("WriterTrumpf not allowed")
            }
        }

        addHistory(table)

        table.trumpf = trumpf
        logger.info(tableid, player.name, "setTrumpf", "set trumpf to $trumpf")
        if (table.puck?.opens === false) {
            table.currentMove = player.position
            logger.info(tableid, player.name, "setTrumpf", "set starting player to ${player.position}")
        } else {
            table.currentMove = table.puck?.position ?: player.position
            logger.info(tableid, player.name, "setTrumpf", "set starting player to ${table.currentMove} due to puck opens")
        }
        table.state = TableState.PLAYING
        logger.info(tableid, player.name, "setTrumpf", "changed state to PLAYING")

        // search for stoeckable player
        table.players.forEach {
            if (table.logic.hasStoecke(it.cards, trumpf)) {
                it.stoecke = Stoeckability.Callable
                logger.info(tableid, player.name, "setTrumpf", "player ${it.name} is stockeable")
            }
        }

        if (table.writer !== null) {
            val teamIdx = player.position % table.logic.amountTeams()
            val writerTrumpf = WriterTrumpf.fromTrumpf(trumpf, joker)
            table.writer.anounce(teamIdx, writerTrumpf)
            logger.info(tableid, player.name, "setTrumpf", "writer anounces $teamIdx with trumpf ${writerTrumpf.value}")
        }

        gameRepo.writeBack(table)
        notifyService.gameUpdate(table)
    }

    fun weis(tableid: String, playerid: String, cards: List<Card>): List<Weis> {
        val table = gameRepo.lockedRead(tableid)
        val player = getPlayer(table, playerid)

        logger.info(tableid, player.name, "weis", "wants to weis the cards ${cards.map { it.toString() }.stream().collect(Collectors.joining(", "))}")

        if (table.state !== TableState.PLAYING) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "weis", "tried to weis in state ${table.state}")
            throw RuntimeException("Weis no possible at this time")
        }

        if (player!!.cards.size !== table.logic.amountCards()) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "weis", "tried to weis but with ${player!!.cards.size}/${table.logic.amountCards()} cards")
            throw RuntimeException("Weis only possible if the player hasnt played any card yet")
        }

        // Check if the given weis cards are on the hand
        if (!cards.all { player.cards.contains(it) }) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "weis", "tried to weis with not his own cards")
            throw RuntimeException("Only cards on the hand can be weised")
        }

        val weises = table.logic.cardsToWeis(cards, table.trumpf!!).toMutableList()
        player.weises = weises
        logger.info(tableid, player.name, "weis", "weising: ${player.weises.map { it.toString() }.stream().collect(Collectors.joining(", "))}")

        gameRepo.writeBack(table)

        return weises
    }

    fun stoecke(tableid: String, playerid: String) {
        val table = gameRepo.lockedRead(tableid)
        val player = getPlayer(table, playerid)

        logger.info(tableid, player.name, "stoecke", "wants to call out stoecke")
        if (player.stoecke === Stoeckability.Callable) {
            player.stoecke = Stoeckability.Called
            logger.info(tableid, player.name, "stoecke", "successfully called out stoecke")
        }

        gameRepo.writeBack(table)
    }



    fun play(tableid: String, playerid: String, card: Card) {
        val table = gameRepo.lockedRead(tableid)

        val player = getPlayer(table, playerid)

        if (table.state !== TableState.PLAYING) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "play", "wanted to play in state ${table.state}")
            throw RuntimeException("Wrong table state")
        }

        if (table.currentMove !== player.position) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "play", "not in charge of playing")
            throw RuntimeException("Wrong order in playing")
        }

        if (!table.logic.cardAllowed(table.round, card, player.cards, table.trumpf!!)) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "play", "not allowed to play ${card}")
            throw RuntimeException("Card not allowed to play")
        }

        // Reset weis, if its the first card of the second round
        if (table.players.count { it.cards.size === table.logic.amountCards() - 1 } === table.players.size - 1
                && table.players.count { it.cards.size === table.logic.amountCards() - 2 } === 1) {
            table.players.forEach { it.weises.clear() }
            logger.info(tableid, player.name, "play", "first card of second round played. Reseting all weises")
        }

        table.round.add(card)
        player.cards.remove(card)
        logger.info(tableid, player.name, "play", "played card $card")
        table.currentMove = (table.currentMove!! + 1) % table.logic.amountPlayers()


        // Last card of round
        if (table.round.size === table.logic.amountPlayers()) {
            logger.info(tableid, player.name, "play", "it was the last card of the round")

            // if it was the last card of the first round, find the best weis, remove the others, calc weis points and remove a possible Stoecke
            var bestWeisIdx: Int? = null
            for(i in table.players.indices) {
                val realIdx = (i + table.currentMove!! + table.players.size) % table.players.size
                if (table.players[realIdx].weises.isNotEmpty()) {
                    if (bestWeisIdx === null) {
                        bestWeisIdx = realIdx
                        logger.info(tableid, player.name, "play", "${table.players.find{ it.position === realIdx }!!.name} has the first weis")
                    } else if (table.logic.compareWeis(table.players[realIdx].weises, table.players[bestWeisIdx].weises, table.trumpf!!) > 0) {
                        bestWeisIdx = realIdx
                        logger.info(tableid, player.name, "play", "${table.players.find{ it.position === realIdx }!!.name} has a better weis")
                    }
                }
            }
            if (bestWeisIdx !== null) {
                // THERE ARE SOME WEISES
                logger.info(tableid, player.name, "play", "there was a weis detected")
                // remove the weises from the opponents and calculate the weis points to the winners
                for (i in table.players.indices) {
                    if (bestWeisIdx % table.logic.amountTeams() !== i % table.logic.amountTeams()) {
                        table.players[i].weises.clear()
                    }

                    // calc weis points for each player
                    var points = table.logic.calcWeissPoints(table.players[i].weises)
                    logger.info(tableid, player.name, "play", "$points calculated for the weis: ${table.players[i].weises.map { it.toString() }.stream().collect(Collectors.joining(", "))}")

                    // Check if there are stoecke in the weis
                    val weisCards = table.logic.weisToCards(table.players[i].weises)
                    if (table.logic.hasStoecke(weisCards, table.trumpf!!)) {
                        table.players[i].stoecke = Stoeckability.Called
                        logger.info(tableid, player.name, "play", "stoecke detected in the weis")
                    }

                    table.weisPoints[i % table.logic.amountTeams()] += points
                }
            }

            // round calculations
            val nextMove = (table.currentMove!! + table.logic.roundWinner(table.round, table.trumpf!!)) % table.players.size
            var points = table.logic.calcPoints(table.round, table.trumpf!!)
            val winningTeamIndex = nextMove % table.logic.amountTeams()
            logger.info(tableid, player.name, "play", "$points calculated for the round with the cards: ${table.round.map { it.toString() }.stream().collect(Collectors.joining(", "))}")

            // store last round
            table.roundHistory.add(RoundHistory(table.currentMove!!, table.round.toList(), nextMove, winningTeamIndex))

            // last round
            if (table.players.all { it.cards.isEmpty() }) {
                points += 5
                logger.info(tableid, player.name, "play", "last round, adding 5 points -> $points")
                if (table.roundHistory.all { it.teamIndex === winningTeamIndex }) {
                    points += 100
                    logger.info(tableid, player.name, "play", "matsch round, adding 100 points -> $points")
                }
                table.state = TableState.FINISHED
                logger.info(tableid, player.name, "play", "changed state to FINISHED")
            }

            // assign points
            table.points[winningTeamIndex] += points
            logger.info(tableid, player.name, "play", "adding points to team ${nextMove % table.logic.amountTeams()} -> in total ${table.points[nextMove % table.logic.amountTeams()]}")

            if (table.players.all { it.cards.isEmpty() } && table.writer !== null) {
                // writer calculations
                val matsch = table.roundHistory.all { it.teamIndex === winningTeamIndex }
                val kontraMatsch = matsch && winningTeamIndex !== table.writer.currentTeam
                logger.info(tableid, player.name, "gameEnd", "Matsch=$matsch, KontraMatsch=$kontraMatsch")
                if (matsch) {
                    table.writer.matsch(kontraMatsch)
                } else {
                    table.writer.write(table.points[table.writer.currentTeam!!])
                }
                for (i in 0 until table.logic.amountTeams()) {
                    if (!(matsch && i !== table.writer.currentTeam) && !(kontraMatsch && i === table.writer.currentTeam)) {
                        table.writer.writeWeiss(i, table.weisPoints[i])
                        logger.info(tableid, " -- ", "gameEnd", "Writing weis for team $i: ${table.weisPoints[i]}")
                    }
                }
                val stoeckePlayer = table.players.find { it.stoecke === Stoeckability.Called }
                if (stoeckePlayer !== null) {
                    table.writer.writeStoecke(stoeckePlayer.position % table.logic.amountTeams())
                    logger.info(tableid, " -- ", "gameEnd", "Writing stoecke for team ${stoeckePlayer.position % table.logic.amountTeams()}")
                }
            }

            table.currentMove = nextMove
            table.round.clear()
            logger.info(tableid, player.name, "play", "next round starts with position $nextMove")

            // flip kulmi
            if (table.trumpf === Trumpf.KulmiUnten) {
                table.trumpf = Trumpf.KulmiOben
                logger.info(tableid, player.name, "play", "flipped kulmi to oben")
            } else if (table.trumpf === Trumpf.KulmiOben) {
                table.trumpf = Trumpf.KulmiUnten
                logger.info(tableid, player.name, "play", "flipped kulmi to unten")
            }
        }

        gameRepo.writeBack(table)
        notifyService.gameUpdate(table)
    }


    fun undo(tableid: String, playerid: String) {
        var table = gameRepo.lockedRead(tableid)

        val player = getPlayer(table, playerid)

        logger.info(tableid, player.name, "undo", "wants to undo")
        if (table.state === TableState.TRUMPF) {
            val cardDistribution = table.players.map { it.cards.toMutableList() }.toMutableList()
            table = table.history!!
            table.cardDistribution = cardDistribution
            logger.info(tableid, player.name, "undo", "undo and back to the last game")
        } else if (table.state === TableState.PLAYING && table.players.all { it.cards.size === table.logic.amountCards() }) {
            // no cards played yet
            table = table.history!!
            logger.info(tableid, player.name, "undo", "undo the trumpf selection")
        }

        gameRepo.writeBack(table)
        notifyService.gameUpdate(table)
    }

    fun newGame(tableid: String, playerid: String, restart: Boolean) {
        val table = gameRepo.lockedRead(tableid)

        val player = getPlayer(table, playerid)

        if (table.state !== TableState.FINISHED) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, player.name, "newGame", "wants to start a new game in state ${table.state}")
            throw RuntimeException("Wrong state for new game")
        }

        logger.info(tableid, player.name, "newGame", "initializing new game")

        addHistory(table)
        var restart2 = restart
        if (table.writer?.over() === true) {
            table.writer.reset()
            logger.info(tableid, player.name, "newGame", "Writer reset")
            restart2 = true
        }

        val cardDistribution = table.cardDistribution?.toMutableList()
        nextGame(table)
        table.cardDistribution = null

        // if we have to restore the previous card distribution
        if (cardDistribution !== null
                && cardDistribution.size === table.logic.amountPlayers()
                && cardDistribution.all { it.size === table.logic.amountCards() }) {
            logger.info(tableid, player.name, "newGame", "CardDistribution set; Overriding player cards: "
                    + cardDistribution.map { cards -> "{" + cards.map { it.toString() }.joinToString(",") + "}" }.joinToString(","))
            table.players.forEach {
                it.cards = cardDistribution[it.position]
            }
        }

        if (restart2) {
            organizePuck(table)
        } else if (table.puck !== null) {
            table.puck.position = (table.puck.position + 1) % table.logic.amountPlayers()
            logger.info(tableid, player.name, "puck", "Next puck: ${table.puck.position}")
        }
        logger.info(tableid, player.name, "newGame", "started new Game")
        table.players.forEach { logger.info(tableid, it.name, "handCards", it.cards.map { c -> c.toString() }.stream().collect(Collectors.joining(","))) }

        gameRepo.writeBack(table)
        notifyService.gameUpdate(table)
    }

    fun leave(tableid: String, playerid: String) {
        val table = gameRepo.lockedRead(tableid)
        val player = getPlayer(table, playerid)

        // Remove player
        table.players.remove(player)
        logger.info(tableid, player.name, "leave", "Player left")
        table.state = TableState.PENDING

        // delete table if empty
        if (table.players.isEmpty()) {
            gameRepo.delete(tableid)
            logger.info(tableid, player.name, "leave", "Table is empty. Got deleted")
        } else {
            gameRepo.writeBack(table)
        }

        notifyService.loungeUpadte()
        notifyService.gameUpdate(table)
    }

    fun lay(tableid: String, playerid: String) {
        var table = gameRepo.read(tableid)
        val player = table.players.find { it.playerid == playerid }
        logger.info(tableid, "system", "lay", "wants to lay all cards")

        if (player === null || table.state !== TableState.PLAYING || !table.players.all { it.cards.size <= 1 }) {
            logger.warn(tableid, "system", "lay", "not able to lay")
            return
        }

        table.players.forEach {
            val player = table.players.find { it.position == table.currentMove }
            if (player !== null && player.cards.isNotEmpty()) {
                if (player.stoecke === Stoeckability.Callable) {
                    stoecke(tableid, player.playerid)
                }
                play(tableid, player.playerid, player.cards.first())
            }
        }
    }

}