package at.apf.gstochabock.gamelogic.writer

interface GameWriter {
    fun serializeString(): String
    fun import(data: List<List<Pair<Int, Int>>>)
    fun export(): List<List<Pair<Int, Int>>>
    fun init(teams: Int)
    fun trumpfOrder(): List<WriterTrumpf>
    fun openTrumpfs(team: Int): List<WriterTrumpf>
    fun anounce(team: Int, trumpf: WriterTrumpf)
    fun calcPoints(points: Int): Int
    fun write(points: Int)
    fun writeWeiss(team: Int, points: Int)
    fun writeStoecke(team: Int)
}