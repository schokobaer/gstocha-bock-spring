package at.apf.gstochabock.rest

import at.apf.gstochabock.dto.CreateRequestBody
import at.apf.gstochabock.dto.CreateResponseBody
import at.apf.gstochabock.dto.JoinRequestBody
import at.apf.gstochabock.dto.TableDto
import at.apf.gstochabock.mapper.TableMapper
import at.apf.gstochabock.service.LoungeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class LoungeController {

    @Autowired
    lateinit var loungeService: LoungeService

    @Autowired
    lateinit var tableMapper: TableMapper

    @GetMapping("/api/table")
    fun getTables(@RequestHeader playerid: String?): List<TableDto> {
        return (if (playerid === null) loungeService.getOpenTables() else loungeService.getRunningTables(playerid))
            .map { tableMapper.toTableDto(it) }
    }

    @PostMapping("/api/table")
    fun createTable(@RequestHeader playerid: String, @RequestBody req: CreateRequestBody): CreateResponseBody {
        val table = loungeService.createTable(playerid, req.name, req.password, req.logic, req.puck)
        return CreateResponseBody(table.id)
    }

    @PostMapping("/api/table/{id}/join")
    fun join(@PathVariable id: String, @RequestHeader playerid: String, @RequestBody req: JoinRequestBody) {
        loungeService.joinTable(id, playerid, req.name, req.position, req.password)
    }

}