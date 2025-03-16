package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.*
import io.ktor.client.engine.*

expect fun <T : HttpClientEngineConfig> HttpClientConfig<T>.provideContentNegotiation()