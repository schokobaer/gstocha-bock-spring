package at.apf.gstochabock.dto

class CreateRequestBody(
        val name: String,
        val password: String? = null,
        val logic: String = "base",
        val puck: String? = null,
        val writer: String? = null,
        val randomizePlayerOrder: Boolean = false
)

data class JoinRequestBody(
        val name: String,
        val position: Int,
        val password: String? = null
)


data class TrumpfRequestBody(val trumpf: String)
data class WeisRequestBody(val cards: List<String>)
data class PlayRequestBody(val card: String)
data class NewGameRequestBody(val restart: Boolean)