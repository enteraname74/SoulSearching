package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

actual fun <T : HttpClientEngineConfig> HttpClientConfig<T>.provideContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            serializeNulls()
        }
    }
}