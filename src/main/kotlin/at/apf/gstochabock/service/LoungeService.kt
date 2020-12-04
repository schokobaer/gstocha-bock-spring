package at.apf.gstochabock.service

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.model.MutablePair
import at.apf.gstochabock.model.Player
import at.apf.gstochabock.model.Table
import at.apf.gstochabock.model.TableState
import at.apf.gstochabock.repo.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class LoungeService {

    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var tableService: TableService

    @Autowired
    lateinit var notifyService: WebSocketNotifyService

    fun getOpenTables(): List<Table> {
        return gameRepo.list { it.players.size < it.logic.amountPlayers() }
    }

    fun createTestTableT() {
        gameRepo.createTestTable(Table("t", null, mutableListOf(0, 0), mutableListOf(0, 0), null, null, mutableListOf(), null, null,
                mutableListOf(Player("a", "AF", 0, mutableListOf(), mutableListOf(), null)), null, BaseJassLogic()))
    }

    fun createTable(playerid: String, playername: String, password: String?): Table {
        val table = Table("", password, mutableListOf(), mutableListOf(), null, null,
                mutableListOf(), null, null, mutableListOf(), null, BaseJassLogic())
        table.players.add(Player(playerid, playername, 0, mutableListOf(), mutableListOf(), null))
        gameRepo.create(table)
        notifyService.loungeUpadte()
        return table
    }

    fun joinTable(tableid: String, playerid: String, playername: String, position: Int, password: String?) {
        val table = gameRepo.lockedRead(tableid)

        if (table.state !== TableState.PENDING) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Wrong state to join")
        }

        if (table.players.size >= table.logic.amountPlayers()) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Full table")
        }
        if (!table.players.none { it.playerid == playerid }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Player already joined table")
        }
        if (position >= table.logic.amountPlayers() || position < 0) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Invalid position for table")
        }
        if (!table.players.none { it.position === position }) {
            gameRepo.unlock(tableid)
            throw RuntimeException("Table position already set")
        }
        table.players.add(Player(playerid, playername, position, mutableListOf(), mutableListOf(), null))
        table.players.sortBy { it.position }

        // table full
        if (table.players.size === table.logic.amountPlayers()) {
            tableService.nextGame(table)
        }

        gameRepo.writeBack(table)
        notifyService.loungeUpadte()
    }

}