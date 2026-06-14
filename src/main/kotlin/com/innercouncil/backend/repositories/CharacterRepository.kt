package com.innercouncil.backend.repositories

import com.innercouncil.backend.db.DatabaseFactory
import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.CharacterRecord
import com.innercouncil.backend.models.tables.CharactersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class CharacterRepository(
    private val databaseFactory: DatabaseFactory
) {
    suspend fun findAll(): List<CharacterRecord> = databaseFactory.dbQuery {
        CharactersTable.selectAll()
            .orderBy(CharactersTable.code, SortOrder.ASC)
            .map(::toCharacter)
    }

    suspend fun findById(id: UUID): CharacterRecord? = databaseFactory.dbQuery {
        CharactersTable.selectAll()
            .where { CharactersTable.id eq id }
            .limit(1)
            .map(::toCharacter)
            .singleOrNull()
    }

    suspend fun insert(record: CharacterRecord): CharacterRecord = databaseFactory.dbQuery {
        CharactersTable.insert {
            it[id] = record.id
            it[code] = record.code.name
            it[name] = record.name
            it[color] = record.color
            it[description] = record.description
            it[createdAt] = record.createdAt
        }
        record
    }

    private fun toCharacter(row: ResultRow): CharacterRecord = CharacterRecord(
        id = row[CharactersTable.id],
        code = CharacterCode.valueOf(row[CharactersTable.code]),
        name = row[CharactersTable.name],
        color = row[CharactersTable.color],
        description = row[CharactersTable.description],
        createdAt = row[CharactersTable.createdAt],
    )
}
