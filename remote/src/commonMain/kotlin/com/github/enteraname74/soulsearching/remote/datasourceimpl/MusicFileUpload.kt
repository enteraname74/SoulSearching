package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.util.WorkDispatcher
import io.ktor.client.HttpClient

internal expect suspend fun uploadMusicFile(
    client: HttpClient,
    baseUrl: String,
    music: Music,
    workDispatcher: WorkDispatcher,
): SoulResult<CloudMusic>
