package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.soulsearching.remote.model.RemoteLyrics
import com.github.enteraname74.soulsearching.repository.datasource.LyricsRemoteDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LyricsRemoteDataSourceImpl(
    private val client: HttpClient,
) : LyricsRemoteDataSource {
    override suspend fun getLyricsOfSong(music: Music): MusicLyrics? =
        getLyrics(
            music = music,
            withAlbum = true,
        ) ?: getLyrics(
            music = music,
            withAlbum = false,
        )

    private suspend fun getLyrics(music: Music, withAlbum: Boolean): MusicLyrics? =
        try {
            val response = client.get(
                buildUrl(
                    music = music,
                    withAlbum = withAlbum,
                )
            )
            if (response.status.isSuccess()) {
                response.body<RemoteLyrics>().toMusicLyrics()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

    companion object {
        private const val INITIAL_ROUTE = "https://lrclib.net/api/get"

        fun buildUrl(music: Music, withAlbum: Boolean): String {
            val albumField = if (withAlbum) "&album_name=${music.album}" else ""

            return "$INITIAL_ROUTE?track_name=${music.name}&artist_name=${music.artist}$albumField"
                .replace(" ", "%20")
        }
    }
}