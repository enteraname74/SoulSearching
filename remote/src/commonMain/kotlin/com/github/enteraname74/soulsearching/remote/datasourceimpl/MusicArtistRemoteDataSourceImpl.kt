package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.httpclient.safeRequest
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.RemoteMusicArtist
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import java.time.LocalDateTime

class MusicArtistRemoteDataSourceImpl(
    private val client: HttpClient,
) : MusicArtistRemoteDataSource {
    override suspend fun fetchMusicArtistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<MusicArtist>> {
        val result: SoulResult<List<RemoteMusicArtist>> = client.safeRequest {
            get(
                urlString = ServerRoutes.MusicArtist.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.map { songs ->
            songs.map { it.toMusicArtist() }
        }
    }
}