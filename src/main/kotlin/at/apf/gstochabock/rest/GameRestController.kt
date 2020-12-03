package at.apf.gstochabock.rest

import at.apf.gstochabock.dto.TableDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GameRestController {

    @ExperimentalStdlibApi
    @GetMapping("/table")
    fun getAllTables(): List<TableDto> {
        val c: MutableList<TableDto> = LinkedList()
        c.add(TableDto("1", true, emptyList()))
        c.add(TableDto("2", false, emptyList()))
        return c
    }

}