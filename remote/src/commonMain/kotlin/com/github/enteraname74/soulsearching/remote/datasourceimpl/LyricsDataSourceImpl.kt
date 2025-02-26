package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.soulsearching.remote.model.RemoteLyrics
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class LyricsDataSourceImpl(
    private val client: HttpClient,
): LyricsDataSource {
    override suspend fun getLyricsOfSong(music: Music): MusicLyrics? {
        val initialPath = "https://lrclib.net/api/get?track_name=${music.name}&artist_name=${music.artist}"
        val encodedPath = initialPath.replace(" ", "%20")

        return try {
            val response = client.get(encodedPath)
            println("RES: ${response.bodyAsText()}")
            if (response.status.isSuccess()) {
                val res = response.body<RemoteLyrics>().toMusicLyrics()
                println("RES: $res")
                res
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}