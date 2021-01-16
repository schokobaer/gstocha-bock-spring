package at.apf.gstochabock.config

import at.apf.gstochabock.repo.GameRepository
import at.apf.gstochabock.repo.impl.FileGameRepository
import at.apf.gstochabock.repo.impl.InMemoryGameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationArgumentConfig {

    @Bean
    fun gameRepo(@Autowired args: ApplicationArguments): GameRepository {
        if (args.containsOption("in-memory")) {
            return InMemoryGameRepository()
        }
        return FileGameRepository()
    }
}