package at.apf.gstochabock.service

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.gamelogic.DornbirnJassLogic
import at.apf.gstochabock.log.GameEventLogger
import at.apf.gstochabock.model.*
import at.apf.gstochabock.repo.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Service
class LoungeService {

    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var tableService: TableService

    @Autowired
    lateinit var notifyService: WebSocketNotifyService

    @Autowired
    lateinit var logger: GameEventLogger

    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy-HH:mm:ss")

    fun getOpenTables(): List<Table> {
        return gameRepo.list { it.players.size < it.logic.amountPlayers() }
    }

    fun getRunningTables(playerid: String): List<Table> {
        return gameRepo.list { it.players.find { p -> p.playerid == playerid } !== null }
    }

    fun createTable(playerid: String, playername: String, password: String?, logicString: String): Table {
        val logic = if (logicString.equals("base")) BaseJassLogic() else DornbirnJassLogic()
        val creationTime = simpleDateFormat.format(Date())
        val table = Table("", password, mutableListOf(), mutableListOf(), null, null,
                mutableListOf(), mutableListOf(), mutableListOf(), null, creationTime, logic)
        table.players.add(Player(playerid, playername, 0, mutableListOf(), mutableListOf(), Stoeckability.None))
        gameRepo.create(table)
        logger.info(table.id, playername, "createTable", "created table")
        notifyService.loungeUpadte()
        return table
    }

    fun joinTable(tableid: String, playerid: String, playername: String, position: Int, password: String?) {
        val table = gameRepo.lockedRead(tableid)

        if (table.state !== TableState.PENDING) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, playername, "joinTable", "tried to join table in state ${table.state}")
            throw RuntimeException("Wrong state to join")
        }

        if (table.players.size >= table.logic.amountPlayers()) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, playername, "joinTable", "tried to join full table")
            throw RuntimeException("Full table")
        }
        if (!table.players.none { it.playerid == playerid }) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, playername, "joinTable", "tried to join, but already part of table")
            throw RuntimeException("Player already joined table")
        }
        if (position >= table.logic.amountPlayers() || position < 0) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, playername, "joinTable", "tried to join a table on an invalid position")
            throw RuntimeException("Invalid position for table")
        }
        if (!table.players.none { it.position === position }) {
            gameRepo.unlock(tableid)
            logger.warn(tableid, playername, "joinTable", "tried to join a table on an already set position")
            throw RuntimeException("Table position already set")
        }
        table.players.add(Player(playerid, playername, position, mutableListOf(), mutableListOf(), Stoeckability.None))
        table.players.sortBy { it.position }
        logger.info(tableid, playername, "joinTable", "player joined table")

        // table full
        if (table.players.size === table.logic.amountPlayers()) {
            tableService.nextGame(table)
            logger.info(tableid, playername, "joinTable", "initialized next game")
            table.players.forEach { logger.info(tableid, it.name, "handCards", it.cards.map { c -> c.toString() }.stream().collect(Collectors.joining(","))) }
        }

        gameRepo.writeBack(table)
        notifyService.loungeUpadte()
        notifyService.gameUpdate(table)
    }

}