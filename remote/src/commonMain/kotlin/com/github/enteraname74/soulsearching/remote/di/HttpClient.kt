package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun provideHttpClient(): HttpClient =
    HttpClient(CIO) {
        install(Resources)
        installContentNegotiation()
    }

fun <T: HttpClientEngineConfig> HttpClientConfig<T>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}