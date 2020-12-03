package at.apf.gstochabock.repo

import at.apf.gstochabock.model.Table
import org.springframework.stereotype.Repository
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Repository
class GameRepository {

    private val gameStore: MutableMap<String, MutablePair<Table, Lock>> = mutableMapOf()


    fun create(table: Table) : String {
        val uuid = UUID.randomUUID().toString()
        table.id = uuid
        gameStore[uuid] = MutablePair(table, ReentrantLock())
        return uuid
    }

    fun read(id: String) : Table {
        val pair = gameStore[id]
        if (pair !== null) {
            return pair.first
        }
        throw RuntimeException("Could not find table with id $id")
    }

    fun lockedRead(id: String): Table {
        val pair = gameStore[id]
        if (pair !== null) {
            pair.second.lock()
            return pair.first
        }
        throw RuntimeException("Could not find table with id $id")
    }

    fun writeBack(table: Table) {
        val pair = gameStore[table.id]
        if (pair !== null) {
            pair.second.unlock()
            pair.first = table
        }
        throw RuntimeException("Could not find table with id ${table.id}")
    }

    private data class MutablePair<K, V>(var first: K, var second: V)

}