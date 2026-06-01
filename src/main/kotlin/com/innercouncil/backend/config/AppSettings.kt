package com.innercouncil.backend.config

import io.ktor.server.config.ApplicationConfig

data class AppSettings(
    val database: DatabaseSettings,
    val statusThresholds: StatusThresholdSettings
) {
    companion object {
        fun from(config: ApplicationConfig): AppSettings = AppSettings(
            database = DatabaseSettings(
                jdbcUrl = config.readString("app.database.jdbcUrl", "DATABASE_URL", "jdbc:postgresql://localhost:5432/inner_council"),
                username = config.readString("app.database.username", "DATABASE_USERNAME", "inner_council"),
                password = config.readString("app.database.password", "DATABASE_PASSWORD", "inner_council"),
                driverClassName = config.readString("app.database.driverClassName", "DATABASE_DRIVER", "org.postgresql.Driver"),
                maximumPoolSize = config.readInt("app.database.maximumPoolSize", "DATABASE_MAX_POOL_SIZE", 10),
            ),
            statusThresholds = StatusThresholdSettings(
                starvingMax = config.readInt("app.statusThresholds.starvingMax", null, 9),
                hungryMax = config.readInt("app.statusThresholds.hungryMax", null, 29),
                contentMax = config.readInt("app.statusThresholds.contentMax", null, 59),
                happyMax = config.readInt("app.statusThresholds.happyMax", null, 99),
            ),
        )
    }
}

private fun ApplicationConfig.readString(path: String, envName: String?, default: String): String =
    envName?.let(System::getenv) ?: propertyOrNull(path)?.getString() ?: default

private fun ApplicationConfig.readInt(path: String, envName: String?, default: Int): Int =
    envName?.let(System::getenv)?.toIntOrNull() ?: propertyOrNull(path)?.getString()?.toIntOrNull() ?: default
