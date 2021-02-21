package at.apf.gstochabock.gamelogic.writer

import java.lang.RuntimeException


open class BaseWriter : GameWriter {

    private val data: MutableList<MutableMap<WriterTrumpf, WriteEntry>> = mutableListOf()



    override fun serializationCode() = "base"

    override fun import(data: List<List<Pair<Int, Int>>>) {
        for (teamData in data) {
            val map = mutableMapOf<WriterTrumpf, WriteEntry>()
            this.data.add(map)

            var i = 0
            for (trumpf in trumpfOrder()) {
                map[trumpf] = WriteEntry(
                        teamData[i]!!.first !== 0,
                        teamData[i]!!.first,
                        teamData[i]!!.second
                )
                i++
            }
        }
    }

    override fun export(): List<List<Pair<Int, Int>>> {
        val result = mutableListOf<List<Pair<Int, Int>>>()
        for (teamData in data) {
            val list = mutableListOf<Pair<Int, Int>>()
            result.add(list)
            for (t in trumpfOrder()) {
                list.add(Pair(teamData[t]!!.points, teamData[t]!!.weis))
            }
        }
        return result
    }

    override fun init(teams: Int) {
        for (i in 0 until teams) {
            data.add(mutableMapOf())
            for (t in trumpfOrder()) {
                data[i]!![t] = WriteEntry(false, 0, 0)
            }
        }
    }

    override fun trumpfOrder(): List<WriterTrumpf> = listOf(
            WriterTrumpf.Eichel,
            WriterTrumpf.Laub,
            WriterTrumpf.Herz,
            WriterTrumpf.Schell,
            WriterTrumpf.Geiss,
            WriterTrumpf.Bock,
            WriterTrumpf.Sieben,
            WriterTrumpf.Acht,
            WriterTrumpf.Kulmi
    )

    override fun openTrumpfs(team: Int): List<WriterTrumpf> =
        data[team].filter { !it.value.played }.map { it.key }

    override fun anounce(team: Int, trumpf: WriterTrumpf) {
        if (!openTrumpfs(team).contains(trumpf)) {
            throw RuntimeException("Trumpf already played!")
        }
        currentTeam = team
        currentTrumpf = trumpf
        data[team][trumpf]!!.played = true
    }

    fun multiplicator() = trumpfOrder().indexOf(currentTrumpf) + 1

    override fun calcPoints(points: Int): Int {

        return when {
            points === 257 -> 257 * multiplicator()
            points === -257 -> -257 * multiplicator() * 2
            else -> (points - (157 - points)) * multiplicator()
        }
    }

    override fun write(points: Int) {
        data[currentTeam!!][currentTrumpf]!!.points = calcPoints(points)
    }

    override fun matsch(kontra: Boolean) {
        data[currentTeam!!][currentTrumpf]!!.points = calcPoints(if (kontra) -257 else 257)
    }

    override fun writeWeiss(team: Int, points: Int) {
        data[team][currentTrumpf]!!.weis += points * multiplicator()
    }

    override fun writeStoecke(team: Int) {
        data[team][currentTrumpf]!!.weis += 20 * multiplicator()
    }

    override fun over(): Boolean = data.all { it.all { (k,v) -> v.played } }


    override fun reset() {
        data.forEach { teamData -> teamData.forEach {
            it.value.played = false
            it.value.points = 0
            it.value.weis = 0
        } }
    }

    override fun clone(): GameWriter {
        val clone = BaseWriter()
        data.forEach {
            val map = mutableMapOf<WriterTrumpf, WriteEntry>()
            clone.data.add(map)
            for ((k,v) in it) {
                map[k] = WriteEntry(v.played, v.points, v.weis)
            }
        }
        clone.currentTrumpf = currentTrumpf
        clone.currentTeam = currentTeam
        return clone
    }

    override var currentTrumpf: WriterTrumpf? = null
    override var currentTeam: Int? = null

}

data class WriteEntry(
        var played: Boolean,
        var points: Int,
        var weis: Int
)
