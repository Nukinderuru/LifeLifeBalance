package com.innercouncil.backend.services

import com.innercouncil.backend.models.CharacterStatus
import com.innercouncil.backend.config.StatusThresholdSettings

class CharacterStatusService(
    private val thresholds: StatusThresholdSettings,
) {
    fun resolve(score: Int): CharacterStatus = when {
        score <= thresholds.starvingMax -> CharacterStatus.STARVING
        score <= thresholds.hungryMax -> CharacterStatus.HUNGRY
        score <= thresholds.contentMax -> CharacterStatus.CONTENT
        score <= thresholds.happyMax -> CharacterStatus.HAPPY
        else -> CharacterStatus.FLOURISHING
    }
}
