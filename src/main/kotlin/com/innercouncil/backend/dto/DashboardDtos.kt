package com.innercouncil.backend.dto

import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.CharacterStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class TodayDashboardResponse(
    @Contextual val date: LocalDate,
    val characters: List<TodayCharacterDashboardResponse>
)

@Serializable
data class TodayCharacterDashboardResponse(
    @Contextual val id: UUID,
    val code: CharacterCode,
    val name: String,
    val color: String,
    val dailyScore: Int,
    val weeklyScore: Int,
    val status: CharacterStatus,
    val completedWishCount: Int
)

@Serializable
data class WeeklyDashboardResponse(
    @Contextual val startDate: LocalDate,
    @Contextual val endDate: LocalDate,
    val days: List<DailyDashboardEntryResponse>,
    val characters: List<WeeklyCharacterSummaryResponse>
)

@Serializable
data class DailyDashboardEntryResponse(
    @Contextual val date: LocalDate,
    val characters: List<DailyCharacterAggregateResponse>
)

@Serializable
data class DailyCharacterAggregateResponse(
    @Contextual val characterId: UUID,
    val code: CharacterCode,
    val name: String,
    val color: String,
    val score: Int,
    val status: CharacterStatus,
    val completedWishCount: Int
)

@Serializable
data class WeeklyCharacterSummaryResponse(
    @Contextual val characterId: UUID,
    val code: CharacterCode,
    val name: String,
    val color: String,
    val totalScore: Int,
    val averageDailyScore: Double,
    val status: CharacterStatus,
    val completedWishCount: Int,
    val hungryDaysCount: Int,
    val bestDay: BestDayResponse
)

@Serializable
data class BestDayResponse(
    @Contextual val date: LocalDate,
    val score: Int
)
