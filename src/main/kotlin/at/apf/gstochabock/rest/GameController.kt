package at.apf.gstochabock.rest

import at.apf.gstochabock.dto.*
import at.apf.gstochabock.log.GameEventLogger
import at.apf.gstochabock.mapper.TableMapper
import at.apf.gstochabock.model.Card
import at.apf.gstochabock.model.TableState
import at.apf.gstochabock.model.Trumpf
import at.apf.gstochabock.service.TableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.FileNotFoundException
import java.lang.RuntimeException

@RestController
class GameController {

    @Autowired
    lateinit var tableService: TableService

    @Autowired
    lateinit var tableMapper: TableMapper

    @Autowired
    lateinit var logger: GameEventLogger

    @GetMapping("/api/table/{id}")
    fun getGame(@PathVariable id: String, @RequestHeader playerid: String): Any {
        try {
            val game = tableService.getGameTable(id)
            if (game.players.find { it.playerid == playerid } !== null) {
                return tableMapper.toGameDto(game, playerid)
            }

            return tableMapper.toTableDto(game)
        } catch (e: FileNotFoundException) {
            return 0
        }
    }

    @PostMapping("/api/table/{id}/trumpf")
    fun trumpf(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: TrumpfRequestBody) {
        kotlin.runCatching { tableService.setTrumpf(id, playerid, Trumpf.fromValue(req.trumpf), req.joker) }
                    .onFailure { logger.stackTrace(id, it) }
    }

    @PostMapping("/api/table/{id}/weis")
    fun weis(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: WeisRequestBody): WeisResponseBody {
        try {
            val weises = tableService.weis(id, playerid, req.cards.map { Card(it) })
            return WeisResponseBody(weises.map { it.toString() })
        } catch (e: Exception) {
            logger.stackTrace(id, e)
        }
        return WeisResponseBody(listOf())
    }

    @PostMapping("/api/table/{id}/stoecke")
    fun stoecke(@PathVariable id: String, @RequestHeader playerid: String) {
        kotlin.runCatching { tableService.stoecke(id, playerid) }
                .onFailure { logger.stackTrace(id, it) }
    }

    @PostMapping("/api/table/{id}/play")
    fun play(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: PlayRequestBody) {
        kotlin.runCatching { tableService.play(id, playerid, Card(req.card)) }
                .onFailure { logger.stackTrace(id, it) }
    }

    @PostMapping("/api/table/{id}/lay")
    fun lay(@PathVariable id: String, @RequestHeader playerid: String) {
        kotlin.runCatching { tableService.lay(id, playerid) }
                .onFailure { logger.stackTrace(id, it) }
    }

    @PostMapping("/api/table/{id}/undo")
    fun undo(@PathVariable id: String, @RequestHeader playerid: String) {
        kotlin.runCatching { tableService.undo(id, playerid) }
                .onFailure { logger.stackTrace(id, it) }
    }

    @PostMapping("/api/table/{id}/new")
    fun newGame(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: NewGameRequestBody) {
        kotlin.runCatching { tableService.newGame(id, playerid, req.restart) }
                .onFailure { logger.stackTrace(id, it) }
    }

    @DeleteMapping("/api/table/{id}")
    fun leave(@PathVariable id: String, @RequestHeader playerid: String) {
        kotlin.runCatching { tableService.leave(id, playerid) }
                .onFailure { logger.stackTrace(id, it) }
    }

}