package at.apf.gstochabock.serialize

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.gamelogic.DornbirnJassLogic
import at.apf.gstochabock.gamelogic.JassLogic
import at.apf.gstochabock.model.*
import com.google.gson.Gson
import org.springframework.stereotype.Component
import com.google.gson.GsonBuilder




@Component
class TableSerDes {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun toText(table: Table): String {
        val tdo = TableDO(table.password, table.points, table.weisPoints, table.currentMove, table.trumpf, table.round, table.roundHistory, table.players, null, table.created, table.logic.serializationCode(), table.puck, table.state)
        val history = table.history

        if (history !== null) {
            tdo.history = TableDO(history.password, history.points, history.weisPoints, history.currentMove, history.trumpf, history.round, history.roundHistory, history.players, null, table.created, history.logic.serializationCode(), history.puck, history.state)
        }

        return gson.toJson(tdo)
    }

    fun fromText(text: String): Table {
        val tdo = gson.fromJson<TableDO>(text, TableDO::class.java)
        val logic = if (tdo.logic.equals("base")) BaseJassLogic() else DornbirnJassLogic()
        val table = Table("", tdo.password, tdo.points, tdo.weisPoints, tdo.currentMove, tdo.trumpf, tdo.round, tdo.roundHistory, tdo.players, null, tdo.created ?: "N/A", logic, tdo.puck, tdo.state)
        val h = tdo.history
        if (h !== null) {
            table.history = Table("", h.password, h.points, h.weisPoints, h.currentMove, h.trumpf, h.round, h.roundHistory, h.players, null, tdo.created ?: "N/A", logic, h.puck, h.state)
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
        val created: String?,
        val logic: String,
        val puck: Puck?,
        var state: TableState = TableState.PENDING
)