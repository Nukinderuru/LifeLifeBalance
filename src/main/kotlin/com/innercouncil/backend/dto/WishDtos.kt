package com.innercouncil.backend.dto

import com.innercouncil.backend.models.WishCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateWishRequest(
    @Contextual val characterId: UUID,
    val title: String,
    val description: String? = null,
    val points: Int,
    val category: WishCategory,
    val active: Boolean = true
)

@Serializable
data class UpdateWishRequest(
    @Contextual val characterId: UUID,
    val title: String,
    val description: String? = null,
    val points: Int,
    val category: WishCategory,
    val active: Boolean
)

@Serializable
data class WishResponse(
    @Contextual val id: UUID,
    @Contextual val characterId: UUID,
    val title: String,
    val description: String?,
    val points: Int,
    val category: WishCategory,
    val active: Boolean,
    @Contextual val createdAt: Instant,
    @Contextual val updatedAt: Instant
)
