package at.apf.gstochabock.model

data class Player(
        val playerid: String,
        var name: String,
        val position: Int,
        var weises: MutableList<Weis>,
        var cards: MutableList<Card>,
        var stoecke: Stoeckability = Stoeckability.None
)