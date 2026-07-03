package com.github.enteraname74.soulsearching.remote.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient =
    HttpClient {
        block()
    }

internal actual val remoteWorkDispatcher: CoroutineDispatcher = Dispatchers.Default
