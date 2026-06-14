package com.innercouncil.backend.services

import com.innercouncil.backend.models.CharacterCode
import com.innercouncil.backend.models.CharacterRecord
import com.innercouncil.backend.repositories.CharacterRepository
import java.time.Instant
import java.util.UUID

class CharacterSeeder(
    private val characterRepository: CharacterRepository
) {
    suspend fun seedCharacters() {
        val existingCodes = characterRepository.findAll().map { it.code }.toSet()
        predefinedCharacters()
            .filterNot { it.code in existingCodes }
            .forEach { characterRepository.insert(it) }
    }

    private fun predefinedCharacters(): List<CharacterRecord> {
        val createdAt = Instant.now()
        return listOf(
            CharacterRecord(UUID.nameUUIDFromBytes("MAYA".toByteArray()), CharacterCode.MAYA, "Maya", "#6B8E6B", "Needs grounding, softness, and a sense of home.", createdAt),
            CharacterRecord(UUID.nameUUIDFromBytes("ELINA".toByteArray()), CharacterCode.ELINA, "Elina", "#4A5D8F", "Needs learning, reflection, and intellectual clarity.", createdAt),
            CharacterRecord(UUID.nameUUIDFromBytes("TORA".toByteArray()), CharacterCode.TORA, "Tora", "#C97A40", "Needs structure, steadiness, and reliable progress.", createdAt),
            CharacterRecord(UUID.nameUUIDFromBytes("DANA".toByteArray()), CharacterCode.DANA, "Dana", "#D4B04C", "Needs play, movement, and sparks of adventure.", createdAt),
            CharacterRecord(UUID.nameUUIDFromBytes("NAOMI".toByteArray()), CharacterCode.NAOMI, "Naomi", "#C88AA0", "Needs beauty, tenderness, and emotional expression.", createdAt),
        )
    }
}
