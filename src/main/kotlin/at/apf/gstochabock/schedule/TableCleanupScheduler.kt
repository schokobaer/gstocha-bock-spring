package at.apf.gstochabock.schedule

import at.apf.gstochabock.repo.GameRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class TableCleanupScheduler {

    private val LOGGER: Logger = LoggerFactory.getLogger(TableCleanupScheduler::class.java)

    @Autowired
    private lateinit var repo: GameRepository

    @Scheduled(fixedRate = 3600000L) // 1h
    fun cleanupIncompleteTables() {
        LOGGER.info("Starting cleanup procedure")

        val thresholdTimestamp = ZonedDateTime.now().minusHours(1)

        repo.list { it.created !== null && it.created.isNotEmpty() }
            .filter {
                getTimestamp(it.created).isBefore(thresholdTimestamp)
                        && it.players.size < it.logic.amountPlayers()
            }
            .forEach {
                LOGGER.info("Table {} is too long incomplete and will be deleted")
                repo.delete(it.id)
            }

        LOGGER.info("Finished cleanup procedure")
    }

    fun getTimestamp(str: String): ZonedDateTime {
        val day = str.substring(0, 2).toInt()
        val month = str.substring(3, 5).toInt()
        val year = str.substring(6, 10).toInt()
        val hour = str.substring(11, 13).toInt()
        val minute = str.substring(14, 16).toInt()
        val second = str.substring(17, 19).toInt()
        val timestamp = ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.systemDefault())
        return timestamp
    }
}