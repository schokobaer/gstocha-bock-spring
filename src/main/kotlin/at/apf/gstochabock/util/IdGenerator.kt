package at.apf.gstochabock.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class IdGenerator {

    fun nextId(): String {
        return UUID.randomUUID().toString()
    }
}