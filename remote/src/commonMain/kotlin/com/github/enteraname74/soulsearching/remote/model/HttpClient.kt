package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.SoulResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object HttpClientNames {
    const val GENERIC = "Generic"
    const val CLOUD_AUTH = "Cloud"
}

val JSON = Json {
    ignoreUnknownKeys = true
}

fun HttpClient.clearToken() {
    this.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
        .firstOrNull()?.clearToken()
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

suspend inline fun HttpClient.safeReadBytes(
    block: HttpClient.() -> HttpResponse
): SoulResult<ByteArray> = try {
    val response = block(this)
    if (response.status.isSuccess()) {
        SoulResult.Success(response.readBytes())
    } else {
        SoulResult.Error(response.bodyAsText())
    }
} catch (e: Exception) {
    SoulResult.Error(e.message ?: e.toString())
}

suspend fun HttpClient.safeSimpleRequest(
    block: suspend HttpClient.() -> HttpResponse
): SoulResult<String> = try {
    val response = block(this)
    if (response.status.isSuccess()) {
        SoulResult.Success(response.bodyAsText())
    } else {
        SoulResult.Error(response.bodyAsText())
    }
} catch (e: Exception) {
    SoulResult.Error(e.message ?: e.toString())
}