package com.innercouncil.backend.services

import com.innercouncil.backend.ApiException
import com.innercouncil.backend.dto.CreateWishRequest
import com.innercouncil.backend.dto.UpdateWishRequest
import com.innercouncil.backend.dto.WishResponse
import com.innercouncil.backend.models.WishRecord
import com.innercouncil.backend.repositories.CharacterRepository
import com.innercouncil.backend.repositories.WishRepository
import io.ktor.http.HttpStatusCode
import java.time.Instant
import java.util.UUID

class WishService(
    private val wishRepository: WishRepository,
    private val characterRepository: CharacterRepository
) {
    suspend fun list(characterId: UUID?, category: com.innercouncil.backend.models.WishCategory?, active: Boolean?): List<WishResponse> =
        wishRepository.findAll(characterId, category, active).map(::toResponse)

    suspend fun get(id: UUID): WishResponse = toResponse(
        wishRepository.findById(id) ?: throw ApiException(HttpStatusCode.NotFound, "Wish not found"),
    )

    suspend fun create(request: CreateWishRequest): WishResponse {
        validateWishInput(request.title, request.points)
        ensureCharacterExists(request.characterId)

        val now = Instant.now()
        val wish = WishRecord(
            id = UUID.randomUUID(),
            characterId = request.characterId,
            title = request.title.trim(),
            description = request.description?.trim()?.ifBlank { null },
            points = request.points,
            category = request.category,
            active = request.active,
            createdAt = now,
            updatedAt = now,
        )

        return toResponse(wishRepository.insert(wish))
    }

    suspend fun update(id: UUID, request: UpdateWishRequest): WishResponse {
        validateWishInput(request.title, request.points)
        ensureCharacterExists(request.characterId)

        val existing = wishRepository.findById(id) ?: throw ApiException(HttpStatusCode.NotFound, "Wish not found")
        val updated = existing.copy(
            characterId = request.characterId,
            title = request.title.trim(),
            description = request.description?.trim()?.ifBlank { null },
            points = request.points,
            category = request.category,
            active = request.active,
            updatedAt = Instant.now(),
        )

        wishRepository.update(updated)
        return toResponse(updated)
    }

    suspend fun softDelete(id: UUID) {
        val updated = wishRepository.softDelete(id, Instant.now())
        if (!updated) throw ApiException(HttpStatusCode.NotFound, "Wish not found")
    }

    private suspend fun ensureCharacterExists(characterId: UUID) {
        if (characterRepository.findById(characterId) == null) {
            throw ApiException(HttpStatusCode.BadRequest, "Character does not exist")
        }
    }

    private fun validateWishInput(title: String, points: Int) {
        require(title.isNotBlank()) { "Wish title must not be blank" }
        require(points > 0) { "Wish points must be greater than zero" }
    }

    private fun toResponse(record: WishRecord): WishResponse = WishResponse(
        id = record.id,
        characterId = record.characterId,
        title = record.title,
        description = record.description,
        points = record.points,
        category = record.category,
        active = record.active,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt,
    )
}
