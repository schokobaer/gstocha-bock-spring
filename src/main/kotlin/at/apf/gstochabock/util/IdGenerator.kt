package at.apf.gstochabock.util

import org.springframework.stereotype.Component
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
        "wald"
    )

    val rnd = Random(Date().time)

    fun nextId(): String {
        val word = words[rnd.nextInt(words.size)]
        var code = ""
        for (i in 1..4) {
            code += rnd.nextInt(10).toString()
        }
        return "$word-$code"
    }
}