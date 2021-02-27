package at.apf.gstochabock.log

import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Component
class GameEventLogger {

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private lateinit var directory: String

    @Value("\${log.directory}")
    fun setDirectory(directory: String) {
        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }
        this.directory = file.normalize().path
    }

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
        val timestamp = sdf.format(now)
        val log = "[$timestamp] ${fill(lvl, 5)} --- ${fill(player, 15, false)} [${fill(action, 15)}]: $msg\n"
        flush(tableid, log)
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

    fun stackTrace(tableid: String, e: Throwable) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        error(tableid, "", "", sw.toString())
    }

    private fun flush(tableid: String, log: String) {
        val file = File(directory + '/' + tableid + ".log")
        if (!file.exists()) {
            file.createNewFile()
        }
        FileUtils.write(file, log, Charset.forName("UTF-8"), true)
    }

}