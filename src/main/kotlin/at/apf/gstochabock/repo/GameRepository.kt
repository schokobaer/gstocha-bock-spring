package at.apf.gstochabock.repo

import at.apf.gstochabock.model.MutablePair
import at.apf.gstochabock.model.Table
import org.springframework.stereotype.Repository
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Repository
class GameRepository {

    private val gameStore: MutableMap<String, MutablePair<Table, Lock>> = mutableMapOf()

    fun createTestTable(table: Table) {
        gameStore[table.id] = MutablePair<Table, Lock>(table, ReentrantLock())
    }

    fun create(table: Table) : String {
        val uuid = UUID.randomUUID().toString()
        table.id = uuid
        gameStore[uuid] = MutablePair<Table, Lock>(table, ReentrantLock())
        return uuid
    }

    fun list(pred: (Table) -> Boolean): List<Table> {
        val tables = gameStore.values.map { it.first }
        if (pred !== null) {
            return tables.filter { pred.invoke(it) }
        }
        return tables
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

    fun unlock(id: String) {
        val pair = gameStore[id]
        if (pair === null) {
            throw RuntimeException("Could not find table with id $id")
        }
        pair.second.unlock()
    }

    fun writeBack(table: Table) {
        val pair = gameStore[table.id]
        if (pair === null) {
            throw RuntimeException("Could not find table with id ${table.id}")
        }
        pair.second.unlock()
        pair.first = table
    }

}