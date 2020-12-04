package at.apf.gstochabock.service

import at.apf.gstochabock.model.Table
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
class WebSocketNotifyService {

    @Autowired
    lateinit var simp: SimpMessageSendingOperations


    fun loungeUpadte() {
        simp.convertAndSend("/event/lounge", "")
    }

    fun gameUpdate(table: Table) {
        simp.convertAndSend("/event/game/${table.id}", table.id)
    }
}