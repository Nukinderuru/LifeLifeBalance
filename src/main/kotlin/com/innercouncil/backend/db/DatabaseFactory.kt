package com.innercouncil.backend.db

import com.innercouncil.backend.config.DatabaseSettings
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory as LiquibaseDatabaseFactory
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource

class DatabaseFactory(
    private val settings: DatabaseSettings
) {
    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    fun init() {
        if (::dataSource.isInitialized) return

        dataSource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = settings.jdbcUrl
                username = settings.username
                password = settings.password
                driverClassName = settings.driverClassName
                maximumPoolSize = settings.maximumPoolSize
                validate()
            },
        )
        database = Database.connect(dataSource)
        migrate(dataSource)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(db = database) { block() }

    private fun migrate(dataSource: DataSource) {
        dataSource.connection.use { connection ->
            val database = LiquibaseDatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(liquibase.database.jvm.JdbcConnection(connection))
            Liquibase("db/changelog/db.changelog-master.yaml", ClassLoaderResourceAccessor(), database).use { liquibase ->
                liquibase.update()
            }
        }
    }
}
