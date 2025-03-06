package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

actual fun provideHttpClient(): HttpClient =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            gson {
                serializeNulls()
            }
        }
    }