package com.github.enteraname74.soulsearching.features.httpclient.ext

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import java.time.LocalDateTime

actual fun <T : HttpClientEngineConfig> HttpClientConfig<T>.provideContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            serializeNulls()
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        }
    }
}