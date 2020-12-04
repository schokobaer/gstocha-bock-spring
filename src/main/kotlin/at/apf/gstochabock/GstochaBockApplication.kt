package at.apf.gstochabock

import at.apf.gstochabock.gamelogic.BaseJassLogic
import at.apf.gstochabock.model.Table
import at.apf.gstochabock.repo.GameRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@Configuration
class GstochaBockApplication

fun main(args: Array<String>) {
    runApplication<GstochaBockApplication>(*args)
}

@Bean
fun bla(gameRepository: GameRepository): CommandLineRunner {
    return CommandLineRunner {
        val id = gameRepository.create(Table("t", null, mutableListOf(0, 0), mutableListOf(0, 0), null, null, mutableListOf(), null, null,
                mutableListOf(), null, BaseJassLogic()))
        val t = gameRepository.lockedRead(id)
        t.id = "t"
        gameRepository.writeBack(t)
    }
}