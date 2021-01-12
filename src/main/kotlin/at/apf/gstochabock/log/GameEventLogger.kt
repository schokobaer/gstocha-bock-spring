package at.apf.gstochabock.log

import at.apf.gstochabock.repo.LogRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Component
class GameEventLogger {

    @Autowired
    private lateinit var logRepo: LogRepo

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    private fun fill(str: String, n: Int, left: Boolean = true): String {
        var s = str
        while (s.length < n){
            s = if (left) " $s" else "$s "
        }
        return s
    }

    @Synchronized
    private fun log(lvl: String, tableid: String, player: String, action: String, msg: String) {
        val now = Date()
        val tbid = fill(tableid.substring(0, 8), 8)
        val timestamp = sdf.format(now)
        val log = "[$timestamp] ${fill(lvl, 5)} --- $tbid/${fill(player, 15, false)} [${fill(action, 15)}]: $msg"
        logRepo.get(tableid).add(log)
    }

    fun debug(tableid: String, player: String, action: String, msg: String) {
        log("DEBUG", tableid, player, action, msg)
    }

    fun info(tableid: String, player: String, action: String, msg: String) {
        log("INFO", tableid, player, action, msg)
    }

    fun warn(tableid: String, player: String, action: String, msg: String) {
        log("WARN", tableid, player, action, msg)
    }

    fun error(tableid: String, player: String, action: String, msg: String) {
        log("ERROR", tableid, player, action, msg)
    }

    fun export(tableid: String): String {
        return logRepo.get(tableid).stream().collect(Collectors.joining("\n"))
    }
}