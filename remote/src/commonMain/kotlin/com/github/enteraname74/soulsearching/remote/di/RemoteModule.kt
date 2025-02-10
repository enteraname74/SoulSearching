package com.github.enteraname74.soulsearching.remote.di

import com.github.enteraname74.soulsearching.remote.datasourceimpl.LyricsDataSourceImpl
import com.github.enteraname74.soulsearching.remote.datasourceimpl.ReleaseDataSourceImpl
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteModule = module {
    factory {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    factoryOf(::LyricsDataSourceImpl) bind LyricsDataSource::class
    factoryOf(::ReleaseDataSourceImpl) bind ReleaseDataSource::class
}