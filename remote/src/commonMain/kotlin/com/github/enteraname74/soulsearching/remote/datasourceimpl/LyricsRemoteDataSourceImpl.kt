package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyricsRequest
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.model.RemoteLyrics
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class LyricsRemoteDataSourceImpl(
    private val client: HttpClient,
) : LyricsRemoteDataSource {
    override suspend fun getLyricsOfSong(request: MusicLyricsRequest): MusicLyrics? =
        fetchLyrics(
            request = request,
            withAlbum = true,
        ) ?: fetchLyrics(
            request = request,
            withAlbum = false,
        )

    private suspend fun fetchLyrics(request: MusicLyricsRequest, withAlbum: Boolean): MusicLyrics? =
        client.safeRequest<RemoteLyrics> {
            get(
                buildUrl(
                    request = request,
                    withAlbum = withAlbum,
                )
            )
        }.getOrNull()?.toMusicLyrics()

    companion object {
        private const val INITIAL_ROUTE = "https://lrclib.net/api/get"

        fun buildUrl(request: MusicLyricsRequest, withAlbum: Boolean): String {
            val albumField = if (withAlbum) "&album_name=${request.albumName}" else ""

            return "$INITIAL_ROUTE?track_name=${request.musicName}&artist_name=${request.artistName}$albumField"
                .replace(" ", "%20")
        }
    }
}
