package com.innercouncil.backend.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object CharactersTable : Table("characters") {
    val id = uuid("id")
    val code = varchar("code", 50)
    val name = varchar("name", 100)
    val color = varchar("color", 20)
    val description = text("description")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object WishesTable : Table("wishes") {
    val id = uuid("id")
    val characterId = uuid("character_id").references(CharactersTable.id)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val points = integer("points")
    val category = varchar("category", 20)
    val active = bool("active")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object CompletionsTable : Table("completions") {
    val id = uuid("id")
    val wishId = uuid("wish_id").references(WishesTable.id)
    val completedDate = date("completed_date")
    val notes = text("notes").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
