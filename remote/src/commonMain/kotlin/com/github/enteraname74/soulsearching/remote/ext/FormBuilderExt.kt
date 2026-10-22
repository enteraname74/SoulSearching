package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import io.ktor.client.request.forms.FormBuilder
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.quote
import kotlinx.serialization.json.Json

inline fun <reified T> FormBuilder.appendJson(
    key: String,
    value: T,
) {
    append(
        key = key,
        value = Json.encodeToString(value),
        headers = Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    )
}

fun FormBuilder.appendCoverData(key: String, data: ByteArray) {
    append(
        key = key,
        value = data,
        headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=${"$key.jpg".quote()}")
            append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
        },
    )
}

expect suspend fun FormBuilder.appendCoverFile(
    key: String,
    path: String,
    workDispatcher: WorkDispatcher,
)
