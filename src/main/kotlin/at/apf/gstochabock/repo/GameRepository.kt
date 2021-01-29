package at.apf.gstochabock.repo

import at.apf.gstochabock.model.Table

interface GameRepository {
    fun createTestTable(table: Table)
    fun create(table: Table) : String
    fun list(pred: (Table) -> Boolean): List<Table>
    fun read(id: String) : Table
    fun lockedRead(id: String): Table
    fun unlock(id: String)
    fun writeBack(table: Table)
    fun delete(id: String)
}