package com.innercouncil.backend.services

import com.innercouncil.backend.ApiException
import com.innercouncil.backend.dto.CharacterResponse
import com.innercouncil.backend.dto.CharacterSummaryResponse
import com.innercouncil.backend.dto.CompletedWishResponse
import com.innercouncil.backend.dto.WishResponse
import com.innercouncil.backend.models.CharacterRecord
import com.innercouncil.backend.repositories.CharacterRepository
import com.innercouncil.backend.repositories.CompletionRepository
import com.innercouncil.backend.repositories.WishRepository
import io.ktor.http.HttpStatusCode
import java.time.LocalDate
import java.util.UUID

class CharacterService(
    private val characterRepository: CharacterRepository,
    private val completionRepository: CompletionRepository,
    private val wishRepository: WishRepository,
    private val statusService: CharacterStatusService
) {
    suspend fun list(): List<CharacterResponse> = characterRepository.findAll().map(::toResponse)

    suspend fun get(id: UUID): CharacterResponse = toResponse(
        characterRepository.findById(id) ?: throw ApiException(HttpStatusCode.NotFound, "Character not found"),
    )

    suspend fun getSummary(id: UUID, date: LocalDate): CharacterSummaryResponse {
        val character = characterRepository.findById(id) ?: throw ApiException(HttpStatusCode.NotFound, "Character not found")
        val dailyCompletions = completionRepository.findByCharacterAndDate(id, date)
        val weeklyCompletions = completionRepository.findByDateRange(date.minusDays(6), date)
            .filter { it.character.id == id }
        val activeWishes = wishRepository.findActiveByCharacterId(id)
        val completedWishIds = dailyCompletions.map { it.wish.id }.toSet()
        val missingWishes = activeWishes.filterNot { it.id in completedWishIds }

        return CharacterSummaryResponse(
            character = toResponse(character),
            dailyScore = dailyCompletions.sumOf { it.wish.points },
            weeklyScore = weeklyCompletions.sumOf { it.wish.points },
            status = statusService.resolve(weeklyCompletions.sumOf { it.wish.points }),
            completedWishes = dailyCompletions.map {
                CompletedWishResponse(
                    completionId = it.completion.id,
                    wishId = it.wish.id,
                    title = it.wish.title,
                    description = it.wish.description,
                    points = it.wish.points,
                    completedDate = it.completion.completedDate,
                    notes = it.completion.notes,
                )
            },
            missingWishes = missingWishes.map {
                WishResponse(
                    id = it.id,
                    characterId = it.characterId,
                    title = it.title,
                    description = it.description,
                    points = it.points,
                    category = it.category,
                    active = it.active,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                )
            },
        )
    }

    private fun toResponse(record: CharacterRecord): CharacterResponse = CharacterResponse(
        id = record.id,
        code = record.code,
        name = record.name,
        color = record.color,
        description = record.description,
        createdAt = record.createdAt,
    )
}
