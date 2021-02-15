package at.apf.gstochabock.serialize

import java.util.stream.Collectors

class GameWriterSerDes {

    fun fromText(text: String): List<List<Pair<Int, Int>>> {
        return text.split("*")
                .map { it.split(";")
                        .map { t -> Pair(
                                t.split(",")[0].toInt(),
                                t.split(",")[1].toInt()
                        ) }
                }
    }

    fun toText(data: List<List<Pair<Int, Int>>>): String {
        return data.map { it.map { t -> "${t.first},${t.second}" }.stream()
                .collect(Collectors.joining(";")) }.stream()
                .collect(Collectors.joining("*"))
    }
}