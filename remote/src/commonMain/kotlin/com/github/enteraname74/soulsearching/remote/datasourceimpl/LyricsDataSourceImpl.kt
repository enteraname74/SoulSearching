package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.remote.model.RemoteLyrics
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LyricsDataSourceImpl(
    private val client: HttpClient,
): LyricsDataSource {
    override suspend fun getLyricsOfSong(music: Music): String? {
        val initialPath = "https://lyrist.vercel.app/api/${music.name}/${music.artist}"
        val encodedPath = initialPath.replace(" ", "%20")

        return try {
            val response = client.get(encodedPath)

            if (response.status.isSuccess()) {
                response.body<RemoteLyrics>().lyrics
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}