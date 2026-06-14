package com.innercouncil.backend.models

import kotlinx.serialization.Serializable

@Serializable
enum class CharacterCode {
    MAYA,
    ELINA,
    TORA,
    DANA,
    NAOMI
}

@Serializable
enum class WishCategory {
    DAILY,
    WEEKLY,
    BIG
}

@Serializable
enum class CharacterStatus {
    STARVING,
    HUNGRY,
    CONTENT,
    HAPPY,
    FLOURISHING
}
