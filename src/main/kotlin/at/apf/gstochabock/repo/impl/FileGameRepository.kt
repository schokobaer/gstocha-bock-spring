package at.apf.gstochabock.repo.impl

import at.apf.gstochabock.model.Table
import at.apf.gstochabock.repo.GameRepository
import at.apf.gstochabock.serialize.TableSerDes
import at.apf.gstochabock.util.IdGenerator
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class FileGameRepository : GameRepository {

    private val locks: MutableMap<String, Lock> = mutableMapOf()
    private val cache: Cache<String, Table> = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build()

    @Autowired
    private lateinit var idGenerator: IdGenerator

    @Autowired
    private lateinit var serDes: TableSerDes

    private lateinit var directory: String

    @Value("\${game.directory}")
    fun setDirectory(directory: String) {
        this.directory = directory
        val file = File(directory)
        if (!file.exists()) {
            file.mkdirs()
        }
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
                .filter { it.extension == "json" && it.isFile }
                .map { it.name.removeSuffix(".json") }
                .map { readFile(it) }
                .filter(pred)
    }

    override fun read(id: String): Table {
        return readFile(id)
    }

    override fun lockedRead(id: String): Table {
        synchronized(locks) {
            if (!locks.containsKey(id)) {
                if (!getFile(id).exists()) {
                    throw RuntimeException("No table with id $id")
                }
                locks[id] = ReentrantLock()
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

    override fun delete(id: String) {
        val file = getFile(id)
        if (file.exists()) {
            file.delete()
        }
        if (locks.containsKey(id)) {
            val lock = locks[id]!!
            locks.remove(id)
            lock.unlock()
        }
        if (cache.getIfPresent(id) !== null) {
            cache.invalidate(id)
        }
    }

    private fun getFile(tableid: String): File {
        var f = File(directory)
        return File(f.normalize().path + '/' + tableid + ".json")
    }

    private fun writeFile(table: Table) {
        cache.put(table.id, table)
        val json = serDes.toText(table)
        FileUtils.write(getFile(table.id), json, "UTF-8")
    }

    private fun readFile(tableid: String): Table {
        val t = cache.getIfPresent(tableid)
        if (t !== null) {
            return t
        }

        val json = FileUtils.readFileToString(getFile(tableid), "UTF-8")
        val table = serDes.fromText(json)
        table.id = tableid
        if (table.history !== null) {
            table.history!!.id = tableid
        }
        return table
    }

}