package com.innercouncil.backend.dto

import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.CharacterStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Serializable
data class CharacterResponse(
    @Contextual val id: UUID,
    val code: CharacterCode,
    val name: String,
    val color: String,
    val description: String,
    @Contextual val createdAt: Instant
)

@Serializable
data class CharacterSummaryResponse(
    val character: CharacterResponse,
    val dailyScore: Int,
    val weeklyScore: Int,
    val status: CharacterStatus,
    val completedWishes: List<CompletedWishResponse>,
    val missingWishes: List<WishResponse>
)

@Serializable
data class CompletedWishResponse(
    @Contextual val completionId: UUID,
    @Contextual val wishId: UUID,
    val title: String,
    val description: String?,
    val points: Int,
    @Contextual val completedDate: LocalDate,
    val notes: String?
)
