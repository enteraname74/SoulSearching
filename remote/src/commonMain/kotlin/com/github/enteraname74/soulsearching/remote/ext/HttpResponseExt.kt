package com.github.enteraname74.soulsearching.remote.ext

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T =
    if (status.isSuccess()) {
        body()
    } else {
        throw Exception(bodyAsText())
    }

suspend fun HttpResponse.successOrThrow(): Unit =
    if (!status.isSuccess()) throw Exception(bodyAsText()) else Unit