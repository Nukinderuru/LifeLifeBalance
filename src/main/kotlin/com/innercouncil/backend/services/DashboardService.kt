package com.innercouncil.backend.services

import com.innercouncil.backend.dto.BestDayResponse
import com.innercouncil.backend.dto.DailyCharacterAggregateResponse
import com.innercouncil.backend.dto.DailyDashboardEntryResponse
import com.innercouncil.backend.dto.TodayCharacterDashboardResponse
import com.innercouncil.backend.dto.TodayDashboardResponse
import com.innercouncil.backend.dto.WeeklyCharacterSummaryResponse
import com.innercouncil.backend.dto.WeeklyDashboardResponse
import com.innercouncil.backend.models.CharacterStatus
import com.innercouncil.backend.repositories.CharacterRepository
import com.innercouncil.backend.repositories.CompletionRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class DashboardService(
    private val characterRepository: CharacterRepository,
    private val completionRepository: CompletionRepository,
    private val statusService: CharacterStatusService
) {
    suspend fun getToday(date: LocalDate = LocalDate.now()): TodayDashboardResponse {
        val startDate = date.minusDays(6)
        val characters = characterRepository.findAll()
        val completions = completionRepository.findByDateRange(startDate, date)
        val grouped = completions.groupBy { it.character.id to it.completion.completedDate }

        return TodayDashboardResponse(
            date = date,
            characters = characters.map { character ->
                val daily = grouped[character.id to date].orEmpty()
                val weekly = completions.filter { it.character.id == character.id }
                TodayCharacterDashboardResponse(
                    id = character.id,
                    code = character.code,
                    name = character.name,
                    color = character.color,
                    dailyScore = daily.sumOf { it.wish.points },
                    weeklyScore = weekly.sumOf { it.wish.points },
                    status = statusService.resolve(weekly.sumOf { it.wish.points }),
                    completedWishCount = daily.size
                )
            }
        )
    }

    suspend fun getWeek(endDate: LocalDate = LocalDate.now()): WeeklyDashboardResponse {
        val startDate = endDate.minusDays(6)
        val characters = characterRepository.findAll()
        val completions = completionRepository.findByDateRange(startDate, endDate)
        val days = (0L..6L).map { startDate.plusDays(it) }
        val grouped = completions.groupBy { it.character.id to it.completion.completedDate }

        return WeeklyDashboardResponse(
            startDate = startDate,
            endDate = endDate,
            days = days.map { day ->
                DailyDashboardEntryResponse(
                    date = day,
                    characters = characters.map { character ->
                        val dayCompletions = grouped[character.id to day].orEmpty()
                        val score = dayCompletions.sumOf { it.wish.points }
                        DailyCharacterAggregateResponse(
                            characterId = character.id,
                            code = character.code,
                            name = character.name,
                            color = character.color,
                            score = score,
                            status = statusService.resolve(score),
                            completedWishCount = dayCompletions.size
                        )
                    }
                )
            },
            characters = characters.map { character ->
                val perDayScores = days.associateWith { day -> grouped[character.id to day].orEmpty().sumOf { it.wish.points } }
                val characterCompletions = completions.filter { it.character.id == character.id }
                val totalScore = characterCompletions.sumOf { it.wish.points }
                val bestDayEntry = perDayScores.entries.maxWith(compareBy<Map.Entry<LocalDate, Int>> { it.value }.thenBy { it.key })

                WeeklyCharacterSummaryResponse(
                    characterId = character.id,
                    code = character.code,
                    name = character.name,
                    color = character.color,
                    totalScore = totalScore,
                    averageDailyScore = BigDecimal(totalScore).divide(BigDecimal(7), 1, RoundingMode.HALF_UP).toDouble(),
                    status = statusService.resolve(totalScore),
                    completedWishCount = characterCompletions.size,
                    hungryDaysCount = perDayScores.values.count {
                        val status = statusService.resolve(it)
                        status == CharacterStatus.STARVING || status == CharacterStatus.HUNGRY
                    },
                    bestDay = BestDayResponse(
                        date = bestDayEntry.key,
                        score = bestDayEntry.value
                    )
                )
            }
        )
    }
}
