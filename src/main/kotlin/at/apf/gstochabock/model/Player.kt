package at.apf.gstochabock.model

data class Player(val playerid: String, var name: String, val position: Int, var weises: List<Weis>, var cards: List<Card>, var stoeckeable: Boolean?)