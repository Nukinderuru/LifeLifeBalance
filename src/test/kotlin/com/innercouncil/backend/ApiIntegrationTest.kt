package com.innercouncil.backend

import com.innercouncil.backend.dto.CharacterSummaryResponse
import com.innercouncil.backend.dto.WishResponse
import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.WishCategory
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApiIntegrationTest {
    @Test
    fun `characters endpoint returns seeded characters`() = testApp { client ->
        val characters = client.fetchCharacters()

        assertEquals(5, characters.size)
        assertEquals(
            setOf(CharacterCode.MAYA, CharacterCode.ELINA, CharacterCode.TORA, CharacterCode.DANA, CharacterCode.NAOMI),
            characters.map { it.code }.toSet(),
        )
        assertTrue(characters.any { it.code == CharacterCode.MAYA && it.color == "#6B8E6B" })
    }

    @Test
    fun `wish lifecycle supports create update and soft delete`() = testApp { client ->
        val maya = client.findCharacterByCode(CharacterCode.MAYA)
        val created = client.createWish(maya.id, "Drink tea", 5)

        val updated = client.put("/api/wishes/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody(
                com.innercouncil.backend.dto.UpdateWishRequest(
                    characterId = maya.id,
                    title = "Drink green tea",
                    description = "Warm and quiet",
                    points = 7,
                    category = WishCategory.WEEKLY,
                    active = true,
                ),
            )
        }.body<WishResponse>()

        assertEquals("Drink green tea", updated.title)
        assertEquals(7, updated.points)
        assertEquals(WishCategory.WEEKLY, updated.category)

        val deleteResponse = client.delete("/api/wishes/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val fetched = client.get("/api/wishes/${created.id}").body<WishResponse>()
        assertFalse(fetched.active)
    }

    @Test
    fun `wishes and character summary are sorted by points`() = testApp { client ->
        val maya = client.findCharacterByCode(CharacterCode.MAYA)
        val bigWish = client.createWish(maya.id, "Volunteer at the zoo", 100, WishCategory.BIG)
        val mediumWish = client.createWish(maya.id, "Walk in nature", 25, WishCategory.WEEKLY)
        val smallWish = client.createWish(maya.id, "Drink tea", 5, WishCategory.DAILY)
        val date = LocalDate.of(2026, 6, 1)

        client.createCompletion(bigWish.id, date)
        client.createCompletion(smallWish.id, date)

        val wishes = client.get("/api/wishes?characterId=${maya.id}").body<List<WishResponse>>()
        assertEquals(listOf(5, 25, 100), wishes.map { it.points })

        val summary = client.get("/api/characters/${maya.id}/summary?date=$date").body<CharacterSummaryResponse>()
        assertEquals(listOf(5, 100), summary.completedWishes.map { it.points })
        assertEquals(listOf(25), summary.missingWishes.map { it.points })
    }

    @Test
    fun `completion endpoint rejects duplicate same-day completion`() = testApp { client ->
        val elina = client.findCharacterByCode(CharacterCode.ELINA)
        val wish = client.createWish(elina.id, "Study math", 20)
        val date = LocalDate.of(2026, 6, 1)

        val created = client.createCompletion(wish.id, date, "Solid session")
        assertEquals(wish.id, created.wishId)

        val duplicateResponse = client.post("/api/completions") {
            contentType(ContentType.Application.Json)
            setBody(com.innercouncil.backend.dto.CreateCompletionRequest(wish.id, date, null))
        }

        assertEquals(HttpStatusCode.Conflict, duplicateResponse.status)
        val error = duplicateResponse.body<ApiErrorResponse>()
        assertTrue(error.message.contains("already completed"))
    }

    @Test
    fun `character summary returns completed and missing wishes`() = testApp { client ->
        val maya = client.findCharacterByCode(CharacterCode.MAYA)
        val completedWish = client.createWish(maya.id, "Walk in nature", 15)
        val missingWish = client.createWish(maya.id, "Drink tea", 5)
        val date = LocalDate.of(2026, 6, 1)

        client.createCompletion(completedWish.id, date)

        val summary = client.get("/api/characters/${maya.id}/summary?date=$date").body<CharacterSummaryResponse>()

        assertEquals(15, summary.dailyScore)
        assertEquals(15, summary.weeklyScore)
        assertEquals(1, summary.completedWishes.size)
        assertEquals(completedWish.id, summary.completedWishes.first().wishId)
        assertTrue(summary.missingWishes.any { it.id == missingWish.id })
    }

    @Test
    fun `today dashboard returns current daily and weekly aggregates`() = testApp { client ->
        val today = LocalDate.now()
        val twoDaysAgo = today.minusDays(2)
        val maya = client.findCharacterByCode(CharacterCode.MAYA)
        val elina = client.findCharacterByCode(CharacterCode.ELINA)
        val mayaTodayWish = client.createWish(maya.id, "Tea and stillness", 12)
        val mayaEarlierWish = client.createWish(maya.id, "Garden walk", 25)
        val elinaWish = client.createWish(elina.id, "Write notes", 8)

        client.createCompletion(mayaTodayWish.id, today)
        client.createCompletion(mayaEarlierWish.id, twoDaysAgo)
        client.createCompletion(elinaWish.id, today)

        val dashboard = client.getTodayDashboard()

        assertEquals(today, dashboard.date)
        assertEquals(5, dashboard.characters.size)

        val mayaCard = dashboard.characters.firstOrNull { it.code == CharacterCode.MAYA }
        assertNotNull(mayaCard)
        assertEquals(12, mayaCard.dailyScore)
        assertEquals(37, mayaCard.weeklyScore)
        assertEquals(1, mayaCard.completedWishCount)
        assertEquals(com.innercouncil.backend.models.CharacterStatus.CONTENT, mayaCard.status)

        val elinaCard = dashboard.characters.firstOrNull { it.code == CharacterCode.ELINA }
        assertNotNull(elinaCard)
        assertEquals(8, elinaCard.dailyScore)
        assertEquals(8, elinaCard.weeklyScore)
        assertEquals(1, elinaCard.completedWishCount)
        assertEquals(com.innercouncil.backend.models.CharacterStatus.STARVING, elinaCard.status)
    }

    @Test
    fun `weekly dashboard returns daily and per-character aggregates`() = testApp { client ->
        val maya = client.findCharacterByCode(CharacterCode.MAYA)
        val tora = client.findCharacterByCode(CharacterCode.TORA)
        val mayaWish = client.createWish(maya.id, "Morning walk", 15)
        val toraWish = client.createWish(tora.id, "Dance break", 25)

        client.createCompletion(mayaWish.id, LocalDate.of(2026, 5, 30))
        client.createCompletion(mayaWish.id, LocalDate.of(2026, 6, 1))
        client.createCompletion(toraWish.id, LocalDate.of(2026, 6, 1))

        val dashboard = client.getWeekDashboard(LocalDate.of(2026, 6, 1))

        assertEquals(LocalDate.of(2026, 5, 26), dashboard.startDate)
        assertEquals(LocalDate.of(2026, 6, 1), dashboard.endDate)
        assertEquals(7, dashboard.days.size)
        assertEquals(5, dashboard.days.first().characters.size)

        val mayaSummary = dashboard.characters.firstOrNull { it.code == CharacterCode.MAYA }
        assertNotNull(mayaSummary)
        assertEquals(30, mayaSummary.totalScore)
        assertEquals(2, mayaSummary.completedWishCount)
        assertEquals(LocalDate.of(2026, 6, 1), mayaSummary.bestDay.date)

        val juneFirst = dashboard.days.first { it.date == LocalDate.of(2026, 6, 1) }
        val toraDaily = juneFirst.characters.firstOrNull { it.code == CharacterCode.TORA }
        assertNotNull(toraDaily)
        assertEquals(25, toraDaily.score)
        assertEquals(1, toraDaily.completedWishCount)
    }
}
