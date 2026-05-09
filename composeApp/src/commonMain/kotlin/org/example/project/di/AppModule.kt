package org.example.project.di

import org.example.project.ai.AiRepository
import org.example.project.ai.AiRepositoryImpl
import org.example.project.ai.GeminiService
import org.example.project.data.local.DatabaseDriverFactory
import org.example.project.data.local.NotesDatabaseProvider
import org.example.project.data.local.SettingsManager
import org.example.project.data.remote.HttpClientFactory
import org.example.project.data.remote.RemoteNoteApi
import org.example.project.data.repository.NotesRepository
import org.example.project.platform.BatteryInfo
import org.example.project.platform.DeviceInfo
import org.example.project.platform.NetworkMonitor
import org.koin.dsl.module

fun appModule(databaseDriverFactory: DatabaseDriverFactory) = module {
    single { databaseDriverFactory }
    single { SettingsManager() }
    single { NotesDatabaseProvider.create(get()) }
    single { HttpClientFactory.create() }
    single { RemoteNoteApi(get()) }
    single { GeminiService(get()) }
    single<AiRepository> { AiRepositoryImpl(get()) }
    single { NotesRepository(database = get(), remoteNoteApi = get()) }
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }
}
