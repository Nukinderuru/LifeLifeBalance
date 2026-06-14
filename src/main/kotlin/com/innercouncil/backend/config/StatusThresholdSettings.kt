package com.innercouncil.backend.config

data class StatusThresholdSettings(
    val starvingMax: Int,
    val hungryMax: Int,
    val contentMax: Int,
    val happyMax: Int
)
