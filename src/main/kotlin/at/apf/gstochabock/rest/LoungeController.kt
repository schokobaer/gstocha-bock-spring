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
    fun getOpenTables(): List<TableDto> {
        return loungeService.getOpenTables().map { tableMapper.toTableDto(it) }
    }

    @PostMapping("/api/table")
    fun createTable(@RequestBody req: CreateRequestBody): CreateResponseBody {
        val table = loungeService.createTable(req.playerid, req.name, req.password)
        return CreateResponseBody(table.id)
    }

    @PostMapping("/api/table/{id}/join")
    fun join(@PathVariable id: String, @RequestBody req: JoinRequestBody) {
        loungeService.joinTable(id, req.playerid, req.name, req.position, req.password)
    }

}