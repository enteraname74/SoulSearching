package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.util.WorkDispatcher
import io.ktor.client.request.forms.*

actual suspend fun FormBuilder.appendCoverFile(
    key: String,
    path: String,
    workDispatcher: WorkDispatcher,
) {
    // no-op
}