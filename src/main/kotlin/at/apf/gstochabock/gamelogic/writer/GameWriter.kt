package at.apf.gstochabock.gamelogic.writer

interface GameWriter {
    fun serializationCode(): String
    fun import(data: List<List<Pair<Int, Int>>>)
    fun export(): List<List<Pair<Int, Int>>>
    fun init(teams: Int)
    fun trumpfOrder(): List<WriterTrumpf>
    fun openTrumpfs(team: Int): List<WriterTrumpf>
    fun anounce(team: Int, trumpf: WriterTrumpf)
    fun calcPoints(points: Int): Int
    fun write(points: Int)
    fun matsch(kontra: Boolean)
    fun writeWeiss(team: Int, points: Int)
    fun writeStoecke(team: Int)
    fun over(): Boolean
    fun reset()
    fun clone(): GameWriter

    var currentTrumpf: WriterTrumpf?
    var currentTeam: Int?
}