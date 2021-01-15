package at.apf.gstochabock.repo.impl

import at.apf.gstochabock.model.Table
import at.apf.gstochabock.repo.GameRepository
import at.apf.gstochabock.util.IdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Repository
@Primary
class FileGameRepository : GameRepository {

    private val locks: MutableMap<String, Lock> = mutableMapOf()

    @Autowired
    private lateinit var idGenerator: IdGenerator

    override fun createTestTable(table: Table) {
        TODO("Not yet implemented")
    }

    override fun create(table: Table): String {
        val id = idGenerator.nextId()
        table.id = id
        synchronized(locks) {
            locks[id] = ReentrantLock()
            locks[id]!!.lock()
        }

        // write file

        locks[id]!!.unlock()
        return id
    }

    override fun list(pred: (Table) -> Boolean): List<Table> {
        TODO("Not yet implemented")
    }

    override fun read(id: String): Table {
        TODO("Not yet implemented")
    }

    override fun lockedRead(id: String): Table {
        TODO("Not yet implemented")
    }

    override fun unlock(id: String) {
        TODO("Not yet implemented")
    }

    override fun writeBack(table: Table) {
        TODO("Not yet implemented")
    }

    private fun writeFile(table: Table) {

    }

    private fun readFile(tableid: String): Table {
        TODO("Read table and convert")
    }

}