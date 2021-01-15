package at.apf.gstochabock.repo.impl

import at.apf.gstochabock.model.Table
import at.apf.gstochabock.repo.GameRepository
import at.apf.gstochabock.serialize.TableSerDes
import at.apf.gstochabock.util.IdGenerator
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.lang.RuntimeException
import java.nio.file.Files
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Repository
@Primary
class FileGameRepository : GameRepository {

    private val locks: MutableMap<String, Lock> = mutableMapOf()

    @Autowired
    private lateinit var idGenerator: IdGenerator

    @Autowired
    private lateinit var serDes: TableSerDes

    @Value("game.directory")
    private lateinit var directory: String

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

        writeFile(table)

        unlock(id)
        return id
    }

    override fun list(pred: (Table) -> Boolean): List<Table> {
        return File(directory).listFiles()
                .filter { it.endsWith(".json") && it.isFile }
                .map { it.name.removeSuffix(".json") }
                .map { readFile(it) }
    }

    override fun read(id: String): Table {
        return readFile(id)
    }

    override fun lockedRead(id: String): Table {
        synchronized(locks) {
            if (!locks.containsKey(id)) {
                throw RuntimeException("No table with id $id")
            }
            locks[id]!!.lock()
        }

        try {
            return readFile(id)
        } catch (e: IOException) {
            unlock(id)
            throw RuntimeException(e)
        }
    }

    override fun unlock(id: String) {
        synchronized(locks) {
            if (!locks.containsKey(id)) {
                throw RuntimeException("No table with id $id")
            }
            locks[id]!!.unlock()
        }
    }

    override fun writeBack(table: Table) {
        try {
            writeFile(table)
        } finally {
            unlock(table.id)
        }
    }

    private fun writeFile(table: Table) {
        val json = serDes.toText(table)
        FileUtils.write(File(directory + table.id + ".json"), json, "UTF-8")
    }

    private fun readFile(tableid: String): Table {
        val json = FileUtils.readFileToString(File(directory + tableid + ".json"), "UTF-8")
        val table = serDes.fromText(json)
        table.id = tableid
        if (table.history !== null) {
            table.history!!.id = tableid
        }
        return table
    }

}