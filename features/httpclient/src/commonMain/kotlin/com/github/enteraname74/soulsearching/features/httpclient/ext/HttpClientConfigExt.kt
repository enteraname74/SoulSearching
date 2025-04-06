package com.github.enteraname74.soulsearching.features.httpclient.ext

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig

expect fun <T : HttpClientEngineConfig> HttpClientConfig<T>.provideContentNegotiation()