package at.apf.gstochabock.mapper

import at.apf.gstochabock.dto.TableDto
import at.apf.gstochabock.model.Table
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TableMapper {

    @Autowired
    lateinit var playerMapper: PlayerMapper

    fun toTableDto(t: Table): TableDto {
        return TableDto(t.id, t.password !== null, t.players.map { playerMapper.toTablePlayerDto(it) })
    }

}