package at.apf.gstochabock.service

import at.apf.gstochabock.dto.Trumpf
import at.apf.gstochabock.model.*
import at.apf.gstochabock.repo.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class TableService {

    @Autowired
    lateinit var gameRepo: GameRepository

    fun nextGame(table: Table) {
        table.currentMove = null
        table.points.first = 0
        table.points.second = 0
        table.round.clear()
        table.lastRound = null
        table.matschable = null
        table.history = null
        table.trumpf = null


        val playercards: List<List<Card>> = table.logic.assignCards()
        for (i in playercards.indices) {
            table.players[i].cards = playercards[i]
        }
    }

    fun setTrumpf(tableid: String, startingPlayerid: String, trumpf: Trumpf) {
        val table = gameRepo.lockedRead(tableid)
        if (table.state !== TableState.TRUMPF) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Trumpf not allowed")
        }
        if (!table.logic.allowedTrumpfs().contains(trumpf)) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Trumpf not allowed")
        }
        if (!table.players.none { it.playerid == startingPlayerid }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Player not at table")
        }

        table.trumpf = trumpf
        table.currentMove = table.players.find { it.playerid == startingPlayerid }!!.position
        // TODO: Something to make the undo of the trumpf selection possible
        table.state = TableState.PLAYING

        // search for stoeckable player
        table.players.forEach {
            if (table.logic.hasStoecke(it.cards, trumpf)) {
                it.stoeckeable = false
            }
        }

        gameRepo.writeBack(table)
    }

    fun weis(tableid: String, playerid: String, cards: List<Card>) {
        val table = gameRepo.lockedRead(tableid)

        if (!table.players.none { it.playerid == playerid }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Player not at table")
        }
        if (table.state !== TableState.PLAYING) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Weis no possible at this time")
        }

        val player = table.players.find { it.playerid == playerid }!!
        if (player!!.cards.size !== table.logic.amountCards()) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Weis only possible if the player hasnt played any card yet")
        }

        // Check if the given weis cards are on the hand
        if (!cards.all { player.cards.contains(it) }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Only cards on the hand can be weised")
        }

        player.weises = table.logic.cardsToWeis(cards)

        gameRepo.writeBack(table)
    }

    fun stoecke(tableid: String, playerid: String) {
        val table = gameRepo.lockedRead(tableid)

        if (!table.players.none { it.playerid == playerid }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Player not at table")
        }

        val player = table.players.find { it.playerid == playerid }!!
        if (player.stoeckeable === false) {
            player.stoeckeable = true
        }

        gameRepo.writeBack(table)
    }



    fun play(tableid: String, playerid: String, card: Card) {
        val table = gameRepo.lockedRead(tableid)

        if (!table.players.none { it.playerid == playerid }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Player not at table")
        }
        val player = table.players.find { it.playerid == playerid }!!

        if (table.state !== TableState.PLAYING) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Wrong table state")
        }

        if (table.currentMove !== player.position) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Wrong order in playing")
        }

        if (!table.logic.cardAllowed(table.round, card, player.cards, table.trumpf!!)) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Card not allowed to play")
        }

        // Reset weis, if its the first card of the second round
        if (table.players.count { it.cards.size === table.logic.amountCards() - 1 } === table.players.size - 1
                && table.players.count { it.cards.size === table.logic.amountCards() - 2 } === 1) {
            table.players.forEach { it.weises.clear() }
        }

        table.round.add(card)
        player.cards.remove(card)
        table.currentMove = (table.currentMove!! + 1) % table.logic.amountPlayers()


        // Last card of round
        if (table.round.size === table.logic.amountPlayers()) {
            // if it was the last card of the first round, find the best weis, remove the others, calc weis points and remove a possible Stoecke
            var bestWeisIdx: Int? = null
            for(i in table.players.indices) {
                val realIdx = (i + table.currentMove!! + table.players.size) % table.players.size
                if (table.players[realIdx].weises.isNotEmpty()) {
                    if (bestWeisIdx === null) {
                        bestWeisIdx = realIdx
                    } else if (table.logic.compareWeis(table.players[realIdx].weises, table.players[bestWeisIdx].weises, table.trumpf!!) > 0) {
                        bestWeisIdx = realIdx
                    }
                }
            }
            if (bestWeisIdx !== null) {
                // remove the weises from the opponents and calculate the weis points to the winners
                for (i in table.players.indices) {
                    if (bestWeisIdx % 2 === 0 && i % 2 === 1
                            || bestWeisIdx % 2 === 1 && i % 2 === 0) {
                        table.players[i].weises.clear()
                    }

                    // calc weis points for each player
                    var points = table.logic.calcWeissPoints(table.players[i].weises)

                    // Check if there are stoecke in the weis
                    val weisCards = table.logic.weisToCards(table.players[i].weises)
                    if (table.logic.hasStoecke(weisCards, table.trumpf!!)) {
                        points += 20
                        table.players[i].stoeckeable = true
                    }


                    if (i % 2 === 0) {
                        table.weisPoints.first += points
                    } else {
                        table.weisPoints.second += points
                    }
                }
            }

            // round calculations
            val nextMove = (table.currentMove!! + table.logic.roundWinner(table.round, table.trumpf!!)) % table.players.size
            var points = table.logic.calcPoints(table.round, table.trumpf!!)

            // check matschable
            if (table.matschable === null) {
                table.matschable = true
            } else if (table.matschable === true && (nextMove % 2) === 1) {
                table.matschable = false
            }

            // store last round
            table.lastRound = RoundHistory(table.currentMove!!, table.round, nextMove)

            // last round
            if (table.players.all { it.cards.isEmpty() }) {
                points += 5
                if (table.matschable === true) {
                    points += 100
                }
                table.state = TableState.FINISHED
            }

            // assign points
            if (nextMove % 2 === 0) {
                table.points.first += points
            } else {
                table.points.second += points
            }

            table.currentMove = nextMove
            table.round.clear()

            // flip kulmi
            if (table.trumpf === Trumpf.KulmiUnten) {
                table.trumpf = Trumpf.KulmiOben
            } else if (table.trumpf === Trumpf.KulmiOben) {
                table.trumpf = Trumpf.KulmiUnten
            }
        }

        gameRepo.writeBack(table)
    }


    fun undo(tableid: String, playerid: String) {

    }

    fun newGame(tableid: String, playerid: String) {

    }

    fun leave(tableid: String, playerid: String) {

    }

}