package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO

internal actual fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient =
    HttpClient(CIO) {
        block()
    }
