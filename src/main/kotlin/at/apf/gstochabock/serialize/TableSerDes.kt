package at.apf.gstochabock.serialize

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.gamelogic.DornbirnJassLogic
import at.apf.gstochabock.gamelogic.writer.BaseWriter
import at.apf.gstochabock.model.*
import org.springframework.stereotype.Component
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired


@Component
class TableSerDes {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    @Autowired
    private lateinit var writerSerDes: GameWriterSerDes

    fun toText(table: Table): String {
        val tdo = TableDO(
                table.password,
                table.points,
                table.weisPoints,
                table.currentMove,
                table.trumpf,
                table.round,
                table.roundHistory,
                table.players,
                null,
                table.created,
                table.logic.serializationCode(),
                table.puck,
                if (table.writer !== null) WriterDO(table.writer.serializationCode(), writerSerDes.toText(table.writer.export())) else null,
                table.randomizePlayerOrder,
                table.state
        )
        val history = table.history

        if (history !== null) {
            tdo.history = TableDO(
                    history.password,
                    history.points,
                    history.weisPoints,
                    history.currentMove,
                    history.trumpf,
                    history.round,
                    history.roundHistory,
                    history.players,
                    null,
                    table.created,
                    history.logic.serializationCode(),
                    history.puck,
                    if (history.writer !== null) WriterDO(history.writer.serializationCode(), writerSerDes.toText(history.writer.export())) else null,
                    table.randomizePlayerOrder,
                    history.state
            )
        }

        return gson.toJson(tdo)
    }

    fun fromText(text: String): Table {
        val tdo = gson.fromJson<TableDO>(text, TableDO::class.java)
        val logic = if (tdo.logic.equals("base")) BaseJassLogic() else DornbirnJassLogic()
        val writer = if (tdo.writer !== null) if (tdo.writer.type.equals("base")) BaseWriter() else null else null
        if (writer !== null) {
            writer.import(writerSerDes.fromText(tdo.writer!!.data))
        }
        val table = Table(
                "",
                tdo.password,
                tdo.points,
                tdo.weisPoints,
                tdo.currentMove,
                tdo.trumpf,
                tdo.round,
                tdo.roundHistory,
                tdo.players,
                null,
                tdo.created ?: "N/A",
                logic,
                tdo.puck,
                writer,
                tdo.randomizePlayerOrder ?: false,
                tdo.state
        )
        val h = tdo.history
        if (h !== null) {
            val historyWriter = if (h.writer !== null) if (h.writer.type.equals("base")) BaseWriter() else null else null
            if (historyWriter !== null) {
                historyWriter.import(writerSerDes.fromText(h.writer!!.data))
            }
            table.history = Table(
                    "",
                    h.password,
                    h.points,
                    h.weisPoints,
                    h.currentMove,
                    h.trumpf,
                    h.round,
                    h.roundHistory,
                    h.players,
                    null,
                    tdo.created ?: "N/A",
                    logic,
                    h.puck,
                    historyWriter,
                    h.randomizePlayerOrder ?: false,
                    h.state
            )
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
        val writer: WriterDO?,
        val randomizePlayerOrder: Boolean?,
        var state: TableState = TableState.PENDING
)

private data class WriterDO(
        val type: String,
        val data: String
)