package at.apf.gstochabock.dto

data class CreateRequestBody(val playerid: String, val name: String, val password: String?)
data class JoinRequestBody(val playerid: String, val name: String, val position: Int, val password: String?)
data class TrumpRequestBody(val playerid: String, val trumpf: Trumpf)
data class WeisRequestBody(val playerid: String, val cards: List<String>)
data class StoeckeRequestBody(val playerid: String)
data class PlayRequestBody(val playerid: String, val card: String)
data class UndoRequestBody(val playerid: String)
data class NewRequestBody(val playerid: String)