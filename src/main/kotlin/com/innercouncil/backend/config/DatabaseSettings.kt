package com.innercouncil.backend.config

data class DatabaseSettings(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val maximumPoolSize: Int
)
