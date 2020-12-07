package at.apf.gstochabock.rest

import at.apf.gstochabock.dto.*
import at.apf.gstochabock.mapper.TableMapper
import at.apf.gstochabock.model.Card
import at.apf.gstochabock.model.Trumpf
import at.apf.gstochabock.service.TableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class GameController {

    @Autowired
    lateinit var tableService: TableService

    @Autowired
    lateinit var tableMapper: TableMapper

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
    fun weis(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: WeisRequestBody) {
        tableService.weis(id, playerid, req.cards.map { Card(it) })
    }

    @PostMapping("/api/table/{id}/stoecke")
    fun stoecke(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.stoecke(id, playerid)
    }

    @PostMapping("/api/table/{id}/play")
    fun play(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: PlayRequestBody) {
        tableService.play(id, playerid, Card(req.card))
    }

    @PostMapping("/api/table/{id}/undo")
    fun undo(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.undo(id, playerid)
    }

    @PostMapping("/api/table/{id}/new")
    fun newGame(@PathVariable id: String, @RequestHeader playerid: String) {
        tableService.newGame(id, playerid)
    }

}