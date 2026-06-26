package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.di.currentLanguage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

fun HttpClient.withUrl(url: String): HttpClient =
    config {
        installOrReplace(DefaultRequest) {
            url(url)
            header(HttpHeaders.AcceptLanguage, currentLanguage())
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

suspend inline fun HttpClient.safeUnitRequest(
    block: HttpClient.() -> HttpResponse
): SoulResult<Unit> = try {
    val response = block(this)
    if (response.status.isSuccess()) {
        SoulResult.Success(Unit)
    } else {
        SoulResult.Error(response.bodyAsText())
    }
} catch (e: Exception) {
    SoulResult.Error(e.message ?: e.toString())
}

fun HttpClient.clearToken() {
    authProvider<BearerAuthProvider>()?.clearToken()
}