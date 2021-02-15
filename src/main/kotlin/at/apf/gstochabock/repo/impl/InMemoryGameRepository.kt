package at.apf.gstochabock.repo.impl

import at.apf.gstochabock.model.Table
import at.apf.gstochabock.repo.GameRepository
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class InMemoryGameRepository : GameRepository {

    private val gameStore: MutableMap<String, MutablePair<Table, Lock>> = mutableMapOf()

    override fun create(table: Table) : String {
        val uuid = UUID.randomUUID().toString()
        table.id = uuid
        gameStore[uuid] = MutablePair<Table, Lock>(table, ReentrantLock())
        return uuid
    }

    override fun list(pred: (Table) -> Boolean): List<Table> {
        val tables = gameStore.values.map { it.first }
        return tables.filter { pred.invoke(it) }
    }

    override fun read(id: String) : Table {
        val pair = gameStore[id]
        if (pair !== null) {
            return pair.first
        }
        throw RuntimeException("Could not find table with id $id")
    }

    override fun lockedRead(id: String): Table {
        val pair = gameStore[id]
        if (pair !== null) {
            pair.second.lock()
            return pair.first
        }
        throw RuntimeException("Could not find table with id $id")
    }

    override fun unlock(id: String) {
        val pair = gameStore[id]
        if (pair === null) {
            throw RuntimeException("Could not find table with id $id")
        }
        pair.second.unlock()
    }

    override fun writeBack(table: Table) {
        val pair = gameStore[table.id]
        if (pair === null) {
            throw RuntimeException("Could not find table with id ${table.id}")
        }
        pair.second.unlock()
        pair.first = table
    }

    override fun delete(id: String) {
        if (gameStore.containsKey(id)) {
            val lock = gameStore[id]!!.second
            gameStore.remove(id)
            lock.unlock()
        }
    }

    data class MutablePair<K, V>(var first: K, var second: V)
}