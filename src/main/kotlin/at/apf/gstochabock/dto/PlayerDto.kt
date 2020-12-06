package at.apf.gstochabock.dto

data class TablePlayerDto(
        val name: String,
        val position: Int
)

data class GamePlayerDto(
        val name: String,
        val position: Int,
        val weis: List<String>?,
        val weisCall: String? = null
)