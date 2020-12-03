package at.apf.gstochabock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GstochaBockApplication

fun main(args: Array<String>) {
    runApplication<GstochaBockApplication>(*args)
}
