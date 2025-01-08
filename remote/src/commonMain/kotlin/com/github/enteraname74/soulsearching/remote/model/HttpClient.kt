package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.SoulResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

object HttpClientNames {
    const val GENERIC = "Generic"
    const val CLOUD = "Cloud"
}

sealed interface RemoteResult<T> {
    data class Success<T>(val result: T) : RemoteResult<T> {
        override fun toSoulResult(): SoulResult<T> =
            SoulResult.Success(result)
        override fun <R> toSoulResult(mapData: (T) -> R): SoulResult<R> =
            SoulResult.Success(mapData(result))
        override fun toSimpleResult(): SoulResult<Unit> =
            SoulResult.Success(Unit)
    }
    data class Error<T>(val message: String?) : RemoteResult<T> {
        override fun toSoulResult(): SoulResult<T> =
            SoulResult.Error(message)

        override fun <R> toSoulResult(mapData: (T) -> R): SoulResult<R> =
            SoulResult.Error(message)

        override fun toSimpleResult(): SoulResult<Unit> =
            SoulResult.Error(message)
    }

    fun toSoulResult(): SoulResult<T>
    fun <R>toSoulResult(
        mapData: (T) -> R
    ): SoulResult<R>
    fun toSimpleResult(): SoulResult<Unit>
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