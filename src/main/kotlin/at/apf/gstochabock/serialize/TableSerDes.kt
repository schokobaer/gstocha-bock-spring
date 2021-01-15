package at.apf.gstochabock.serialize

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.gamelogic.JassLogic
import at.apf.gstochabock.model.*
import com.google.gson.Gson
import org.springframework.stereotype.Component

@Component
class TableSerDes {

    private val gson = Gson()

    fun toText(table: Table): String {
        val tdo = TableDO(table.password, table.points, table.weisPoints, table.currentMove, table.trumpf, table.round, table.roundHistory, table.players, null, table.logic.serializationCode(), table.state)
        val history = table.history

        if (history !== null) {
            tdo.history = TableDO(history.password, history.points, history.weisPoints, history.currentMove, history.trumpf, history.round, history.roundHistory, history.players, null, history.logic.serializationCode(), history.state)
        }

        return gson.toJson(tdo)
    }

    fun fromText(text: String): Table {
        val tdo = gson.fromJson<TableDO>(text, TableDO::class.java)
        val logic = BaseJassLogic()
        val table = Table("", tdo.password, tdo.points, tdo.weisPoints, tdo.currentMove, tdo.trumpf, tdo.round, tdo.roundHistory, tdo.players, null, logic, tdo.state)
        val h = tdo.history
        if (h !== null) {
            table.history = Table("", h.password, h.points, h.weisPoints, h.currentMove, h.trumpf, h.round, h.roundHistory, h.players, null, logic, h.state)
        }

        return table
    }
}


private data class TableDO(
        var password: String?,
        var points: MutableList<Int>,
        var weisPoints: MutableList<Int>,
        var currentMove: Int?,
        var trumpf: Trumpf?,
        var round: MutableList<Card>,
        val roundHistory: MutableList<RoundHistory>,
        var players: MutableList<Player>,
        var history: TableDO?,
        val logic: String,
        var state: TableState = TableState.PENDING
)