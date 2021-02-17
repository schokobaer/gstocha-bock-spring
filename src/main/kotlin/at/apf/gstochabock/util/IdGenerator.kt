package at.apf.gstochabock.util

import at.apf.gstochabock.repo.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import kotlin.random.Random

@Component
class IdGenerator {

    val words = listOf(

        "kaeknoepfle",
        "riebelbibel",
        "funka",

        "stoecke",
        "slalom",
        "trumpf",

        "flaeschafurzer",
        "doppelfisch",
        "strohkopf",
        "elefant",
        "bluamatopf",
        "weli",
        "loewe",
        "schlange",

        "seebruenzler",
        "oberlaender",
        "nueziders",
        "ems",
        "breagaz",
        "dorabira",
        "luschnau",
        "bludaz",
        "feldkirch",
        "montafu",
        "wald",

        "hoerbranz-rules-bitches",
        "domi-stinkt"
    )

    val rnd = Random(Date().time)

    @Autowired
    private lateinit var repo: GameRepository

    fun nextId(): String {
        var id = ""
        do {
            val word = words[rnd.nextInt(words.size)]
            var code = ""
            for (i in 1..4) {
                code += rnd.nextInt(10).toString()
            }
            id = "$word-$code"
        } while (exists(id))
        return id
    }

    fun exists(id: String): Boolean {
        return try {
            repo.read(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}