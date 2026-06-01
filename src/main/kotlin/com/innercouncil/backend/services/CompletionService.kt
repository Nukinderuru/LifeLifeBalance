package com.innercouncil.backend.services

import com.innercouncil.backend.ApiException
import com.innercouncil.backend.dto.CompletionResponse
import com.innercouncil.backend.dto.CreateCompletionRequest
import com.innercouncil.backend.models.CompletionRecord
import com.innercouncil.backend.repositories.CharacterRepository
import com.innercouncil.backend.repositories.CompletionRepository
import com.innercouncil.backend.repositories.WishRepository
import io.ktor.http.HttpStatusCode
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class CompletionService(
    private val completionRepository: CompletionRepository,
    private val wishRepository: WishRepository,
    private val characterRepository: CharacterRepository
) {
    suspend fun create(request: CreateCompletionRequest): CompletionResponse {
        val wish = wishRepository.findById(request.wishId) ?: throw ApiException(HttpStatusCode.BadRequest, "Wish does not exist")
        if (!wish.active) throw ApiException(HttpStatusCode.BadRequest, "Cannot complete an inactive wish")
        if (characterRepository.findById(wish.characterId) == null) throw ApiException(HttpStatusCode.BadRequest, "Character does not exist")
        if (completionRepository.existsByWishIdAndDate(request.wishId, request.date)) {
            throw ApiException(HttpStatusCode.Conflict, "Wish is already completed for this date")
        }

        val completion = CompletionRecord(
            id = UUID.randomUUID(),
            wishId = request.wishId,
            completedDate = request.date,
            notes = request.notes?.trim()?.ifBlank { null },
            createdAt = Instant.now(),
        )
        completionRepository.insert(completion)

        return listByDate(request.date).first { it.id == completion.id }
    }

    suspend fun delete(id: UUID) {
        if (!completionRepository.delete(id)) {
            throw ApiException(HttpStatusCode.NotFound, "Completion not found")
        }
    }

    suspend fun listByDate(date: LocalDate): List<CompletionResponse> = completionRepository.findByDate(date).map { detail ->
        CompletionResponse(
            id = detail.completion.id,
            wishId = detail.wish.id,
            characterId = detail.character.id,
            code = detail.character.code,
            characterName = detail.character.name,
            characterColor = detail.character.color,
            wishTitle = detail.wish.title,
            wishCategory = detail.wish.category,
            points = detail.wish.points,
            completedDate = detail.completion.completedDate,
            notes = detail.completion.notes,
            createdAt = detail.completion.createdAt,
        )
    }
}
