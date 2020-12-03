package at.apf.gstochabock.mapper

import at.apf.gstochabock.dto.TablePlayerDto
import at.apf.gstochabock.model.Player
import org.springframework.stereotype.Component

@Component
class PlayerMapper {

    fun toTablePlayerDto(player: Player): TablePlayerDto {
        return TablePlayerDto(player.name, player.position)
    }

}