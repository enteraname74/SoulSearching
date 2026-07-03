package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.ext.appendFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.contentType
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.model.upload.toMusicUpload
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import java.io.File

internal actual suspend fun uploadMusicFile(
    client: HttpClient,
    baseUrl: String,
    music: Music,
): SoulResult<CloudMusic> {
    val file: File = music.localPath
        ?.let { File(it) }
        ?.takeIf { it.exists() } ?: return SoulResult.Error()
    val contentType = file.contentType()

    return client
        .safeRequest {
            submitFormWithBinaryData(
                url = "$baseUrl/music/upload",
                formData = formData {
                    appendFile(
                        key = "file",
                        file = file,
                        contentType = contentType,
                    )
                    appendJson(
                        key = "metadata",
                        value = music.toMusicUpload(),
                    )
                }
            )
        }
}
