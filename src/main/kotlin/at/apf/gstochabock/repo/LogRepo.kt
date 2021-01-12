package at.apf.gstochabock.repo

import org.springframework.stereotype.Repository

@Repository
class LogRepo {

    private val repo: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun create(tableid: String) {
        repo.put(tableid, mutableListOf())
    }

    fun get(tableid: String): MutableList<String> {
        if (!repo.containsKey(tableid)) {
            create(tableid)
        }
        return repo[tableid]!!
    }
}