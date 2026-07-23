package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.util.WorkDispatcher
import io.ktor.client.request.forms.FormBuilder
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

expect suspend fun FormBuilder.appendCoverFile(
    key: String,
    path: String,
    workDispatcher: WorkDispatcher,
)
