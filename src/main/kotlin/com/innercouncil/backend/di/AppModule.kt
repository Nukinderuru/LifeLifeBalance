package com.innercouncil.backend.di

import com.innercouncil.backend.config.AppSettings
import com.innercouncil.backend.db.DatabaseFactory
import com.innercouncil.backend.repositories.CharacterRepository
import com.innercouncil.backend.repositories.CompletionRepository
import com.innercouncil.backend.repositories.WishRepository
import com.innercouncil.backend.services.CharacterSeeder
import com.innercouncil.backend.services.CharacterService
import com.innercouncil.backend.services.CharacterStatusService
import com.innercouncil.backend.services.CompletionService
import com.innercouncil.backend.services.DashboardService
import com.innercouncil.backend.services.WishService
import org.koin.dsl.module

fun appModule(settings: AppSettings) = module {
    single { settings }
    single { DatabaseFactory(settings.database) }

    single { CharacterRepository(get()) }
    single { WishRepository(get()) }
    single { CompletionRepository(get()) }

    single { CharacterStatusService(settings.statusThresholds) }
    single { CharacterSeeder(get()) }
    single { CharacterService(get(), get(), get(), get()) }
    single { WishService(get(), get()) }
    single { CompletionService(get(), get(), get()) }
    single { DashboardService(get(), get(), get()) }
}
