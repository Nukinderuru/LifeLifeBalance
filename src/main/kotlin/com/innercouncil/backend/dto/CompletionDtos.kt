package com.innercouncil.backend.dto

import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.WishCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Serializable
data class CreateCompletionRequest(
    @Contextual val wishId: UUID,
    @Contextual val date: LocalDate,
    val notes: String? = null
)

@Serializable
data class CompletionResponse(
    @Contextual val id: UUID,
    @Contextual val wishId: UUID,
    @Contextual val characterId: UUID,
    val code: CharacterCode,
    val characterName: String,
    val characterColor: String,
    val wishTitle: String,
    val wishCategory: WishCategory,
    val points: Int,
    @Contextual val completedDate: LocalDate,
    val notes: String?,
    @Contextual val createdAt: Instant
)
