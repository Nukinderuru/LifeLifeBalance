package com.innercouncil.backend.repositories

import com.innercouncil.backend.db.DatabaseFactory
import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.CharacterRecord
import com.innercouncil.backend.models.CompletionDetails
import com.innercouncil.backend.models.CompletionRecord
import com.innercouncil.backend.models.WishCategory
import com.innercouncil.backend.models.WishRecord
import com.innercouncil.backend.models.tables.CharactersTable
import com.innercouncil.backend.models.tables.CompletionsTable
import com.innercouncil.backend.models.tables.WishesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.util.UUID

class CompletionRepository(
    private val databaseFactory: DatabaseFactory
) {
    suspend fun existsByWishIdAndDate(wishId: UUID, date: LocalDate): Boolean = databaseFactory.dbQuery {
        CompletionsTable.selectAll()
            .where { (CompletionsTable.wishId eq wishId) and (CompletionsTable.completedDate eq date) }
            .empty()
            .not()
    }

    suspend fun insert(record: CompletionRecord): CompletionRecord = databaseFactory.dbQuery {
        CompletionsTable.insert {
            it[id] = record.id
            it[wishId] = record.wishId
            it[completedDate] = record.completedDate
            it[notes] = record.notes
            it[createdAt] = record.createdAt
        }
        record
    }

    suspend fun delete(id: UUID): Boolean = databaseFactory.dbQuery {
        CompletionsTable.deleteWhere { CompletionsTable.id eq id } > 0
    }

    suspend fun findByDate(date: LocalDate): List<CompletionDetails> = databaseFactory.dbQuery {
        baseJoin()
            .selectAll()
            .where { CompletionsTable.completedDate eq date }
            .orderBy(CharactersTable.code, SortOrder.ASC)
            .map(::toCompletionDetails)
    }

    suspend fun findByCharacterAndDate(characterId: UUID, date: LocalDate): List<CompletionDetails> = databaseFactory.dbQuery {
        baseJoin()
            .selectAll()
            .where { (CharactersTable.id eq characterId) and (CompletionsTable.completedDate eq date) }
            .orderBy(WishesTable.createdAt, SortOrder.ASC)
            .map(::toCompletionDetails)
    }

    suspend fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<CompletionDetails> = databaseFactory.dbQuery {
        baseJoin()
            .selectAll()
            .where { CompletionsTable.completedDate.between(startDate, endDate) }
            .orderBy(CompletionsTable.completedDate, SortOrder.ASC)
            .map(::toCompletionDetails)
    }

    private fun baseJoin() = CompletionsTable
        .innerJoin(WishesTable, { wishId }, { WishesTable.id })
        .innerJoin(CharactersTable, { WishesTable.characterId }, { CharactersTable.id })

    private fun toCompletionDetails(row: ResultRow): CompletionDetails = CompletionDetails(
        completion = CompletionRecord(
            id = row[CompletionsTable.id],
            wishId = row[CompletionsTable.wishId],
            completedDate = row[CompletionsTable.completedDate],
            notes = row[CompletionsTable.notes],
            createdAt = row[CompletionsTable.createdAt],
        ),
        wish = WishRecord(
            id = row[WishesTable.id],
            characterId = row[WishesTable.characterId],
            title = row[WishesTable.title],
            description = row[WishesTable.description],
            points = row[WishesTable.points],
            category = WishCategory.valueOf(row[WishesTable.category]),
            active = row[WishesTable.active],
            createdAt = row[WishesTable.createdAt],
            updatedAt = row[WishesTable.updatedAt],
        ),
        character = CharacterRecord(
            id = row[CharactersTable.id],
            code = CharacterCode.valueOf(row[CharactersTable.code]),
            name = row[CharactersTable.name],
            color = row[CharactersTable.color],
            description = row[CharactersTable.description],
            createdAt = row[CharactersTable.createdAt],
        ),
    )
}
