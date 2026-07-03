package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import io.ktor.client.HttpClient

internal actual suspend fun uploadMusicFile(
    client: HttpClient,
    baseUrl: String,
    music: Music,
): SoulResult<CloudMusic> = SoulResult.Error()
