package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.InputProvider
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.quote
import io.ktor.utils.io.streams.asInput
import java.io.File

inline fun <reified T> FormBuilder.appendJson(
    key: String,
    value: T,
) {
    append(
        key = key,
        value = SerializationUtils.serialize(value),
        headers = Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    )
}

fun FormBuilder.appendFile(
    key: String,
    file: File,
    contentType: String,
) {
    append(
        key = key,
        value = InputProvider(file.length()) {
            file.inputStream().asInput()
        },
        headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=${file.name.quote()}")
            append(HttpHeaders.ContentType, contentType)
        }
    )
}