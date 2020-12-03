package at.apf.gstochabock.dto

data class TablePlayerDto(val name: String, val position: Int)

data class PlayerDto(val name: String, val position: Int, val weis: List<String>?, val cards: List<String>?)