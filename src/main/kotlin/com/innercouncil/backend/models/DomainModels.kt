package com.innercouncil.backend.models

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class CharacterRecord(
    val id: UUID,
    val code: CharacterCode,
    val name: String,
    val color: String,
    val description: String,
    val createdAt: Instant
)

data class WishRecord(
    val id: UUID,
    val characterId: UUID,
    val title: String,
    val description: String?,
    val points: Int,
    val category: WishCategory,
    val active: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class CompletionRecord(
    val id: UUID,
    val wishId: UUID,
    val completedDate: LocalDate,
    val notes: String?,
    val createdAt: Instant
)

data class CompletionDetails(
    val completion: CompletionRecord,
    val wish: WishRecord,
    val character: CharacterRecord
)
