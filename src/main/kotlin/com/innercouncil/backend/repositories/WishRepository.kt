package com.innercouncil.backend.repositories

import com.innercouncil.backend.db.DatabaseFactory
import com.innercouncil.backend.models.WishCategory
import com.innercouncil.backend.models.WishRecord
import com.innercouncil.backend.models.tables.WishesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

class WishRepository(
    private val databaseFactory: DatabaseFactory
) {
    suspend fun findAll(characterId: UUID?, category: WishCategory?, active: Boolean?): List<WishRecord> = databaseFactory.dbQuery {
        WishesTable.selectAll()
            .apply {
                characterId?.let { andWhere { WishesTable.characterId eq it } }
                category?.let { andWhere { WishesTable.category eq it.name } }
                active?.let { andWhere { WishesTable.active eq it } }
            }
            .orderBy(WishesTable.points to SortOrder.ASC, WishesTable.title to SortOrder.ASC, WishesTable.createdAt to SortOrder.ASC)
            .map(::toWish)
    }

    suspend fun findById(id: UUID): WishRecord? = databaseFactory.dbQuery {
        WishesTable.selectAll()
            .where { WishesTable.id eq id }
            .limit(1)
            .map(::toWish)
            .singleOrNull()
    }

    suspend fun findActiveByCharacterId(characterId: UUID): List<WishRecord> = databaseFactory.dbQuery {
        WishesTable.selectAll()
            .where { (WishesTable.characterId eq characterId) and (WishesTable.active eq true) }
            .orderBy(WishesTable.points to SortOrder.ASC, WishesTable.title to SortOrder.ASC, WishesTable.createdAt to SortOrder.ASC)
            .map(::toWish)
    }

    suspend fun insert(record: WishRecord): WishRecord = databaseFactory.dbQuery {
        WishesTable.insert {
            it[id] = record.id
            it[characterId] = record.characterId
            it[title] = record.title
            it[description] = record.description
            it[points] = record.points
            it[category] = record.category.name
            it[active] = record.active
            it[createdAt] = record.createdAt
            it[updatedAt] = record.updatedAt
        }
        record
    }

    suspend fun update(record: WishRecord): Boolean = databaseFactory.dbQuery {
        WishesTable.update({ WishesTable.id eq record.id }) {
            it[characterId] = record.characterId
            it[title] = record.title
            it[description] = record.description
            it[points] = record.points
            it[category] = record.category.name
            it[active] = record.active
            it[updatedAt] = record.updatedAt
        } > 0
    }

    suspend fun softDelete(id: UUID, updatedAt: Instant): Boolean = databaseFactory.dbQuery {
        WishesTable.update({ WishesTable.id eq id }) {
            it[active] = false
            it[WishesTable.updatedAt] = updatedAt
        } > 0
    }

    private fun toWish(row: ResultRow): WishRecord = WishRecord(
        id = row[WishesTable.id],
        characterId = row[WishesTable.characterId],
        title = row[WishesTable.title],
        description = row[WishesTable.description],
        points = row[WishesTable.points],
        category = WishCategory.valueOf(row[WishesTable.category]),
        active = row[WishesTable.active],
        createdAt = row[WishesTable.createdAt],
        updatedAt = row[WishesTable.updatedAt],
    )
}
