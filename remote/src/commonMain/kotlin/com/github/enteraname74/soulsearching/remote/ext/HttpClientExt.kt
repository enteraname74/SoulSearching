package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.model.SoulResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

fun HttpClient.withUrl(url: String): HttpClient =
    config {
        installOrReplace(DefaultRequest) {
            url(url)
        }
    }

suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpClient.() -> HttpResponse
): SoulResult<T> = try {
    val response = block(this)
    if (response.status.isSuccess()) {
        SoulResult.Success(response.body())
    } else {
        SoulResult.Error(response.bodyAsText())
    }
} catch (e: Exception) {
    SoulResult.Error(e.message ?: e.toString())
}

fun HttpClient.clearToken() {
    authProvider<BearerAuthProvider>()?.clearToken()
}