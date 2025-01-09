package com.github.enteraname74.soulsearching.remote.model

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.statement.*
import io.ktor.http.*

object HttpClientNames {
    const val GENERIC = "Generic"
    const val CLOUD = "Cloud"
}

fun HttpClient.clearToken() {
    this.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
        .firstOrNull()?.clearToken()
}

suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpClient.() -> HttpResponse
): RemoteResult<T> = try {
    val response = block(this)
    if (response.status.isSuccess()) {
        RemoteResult.Success(response.body())
    } else {
        RemoteResult.Error(response.bodyAsText())
    }
} catch (e: Exception) {
    RemoteResult.Error(e.message ?: e.toString())
}