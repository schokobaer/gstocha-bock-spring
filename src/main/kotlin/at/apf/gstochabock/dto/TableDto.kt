package at.apf.gstochabock.dto

data class TableDto(
        val id: String,
        val protected: Boolean,
        val players: List<TablePlayerDto>
)