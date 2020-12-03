package at.apf.gstochabock.dto

class CreateRequestBody(val playerid: String, val name: String, val password: String? = null)

data class JoinRequestBody(val playerid: String, val name: String, val position: Int, val password: String? = null)


data class TrumpfRequestBody(val playerid: String, val trumpf: String)
data class WeisRequestBody(val playerid: String, val cards: List<String>)
data class PlayRequestBody(val playerid: String, val card: String)