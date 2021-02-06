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
        val game = tableService.getGameTable(id)
        if (game.players.find { it.playerid == playerid } !== null) {
            return tableMapper.toGameDto(game, playerid)
        }

        return tableMapper.toTableDto(game)
    }

    @PostMapping("/api/table/{id}/trumpf")
    fun trumpf(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: TrumpfRequestBody) {
        tableService.setTrumpf(id, playerid, Trumpf.fromValue(req.trumpf))
    }

    @PostMapping("/api/table/{id}/weis")
    fun weis(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: WeisRequestBody): WeisResponseBody {
        val weises = tableService.weis(id, playerid, req.cards.map { Card(it) })
        return WeisResponseBody(weises.map { it.toString() })
    }

    @PostMapping("/api/table/{id}/stoecke")
    fun stoecke(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.stoecke(id, playerid)
    }

    @PostMapping("/api/table/{id}/play")
    fun play(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: PlayRequestBody) {
        tableService.play(id, playerid, Card(req.card))
    }

    @PostMapping("/api/table/{id}/lay")
    fun lay(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.lay(id, playerid)
    }

    @PostMapping("/api/table/{id}/undo")
    fun undo(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.undo(id, playerid)
    }

    @PostMapping("/api/table/{id}/new")
    fun newGame(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: NewGameRequestBody) {
        tableService.newGame(id, playerid, req.restart)
    }

    @DeleteMapping("/api/table/{id}")
    fun leave(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.leave(id, playerid)
    }

    @GetMapping("/api/table/{id}/log", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getLog(@PathVariable id: String): String {
        val game = tableService.getGameTable(id)
        if (game.state === TableState.FINISHED)
            return logger.export(id)
        return "Game not finished"
    }

}