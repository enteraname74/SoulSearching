package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.util.WorkDispatcher
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.InputProvider
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.quote
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import java.io.File

actual suspend fun FormBuilder.appendCoverFile(
    key: String,
    path: String,
    workDispatcher: WorkDispatcher,
) {
    val file = File(path)

    if (!file.exists()) return

    val contentType = file.contentType(workDispatcher)

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