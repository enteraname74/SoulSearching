package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.RemoteMusicPlaylist
import com.github.enteraname74.soulsearching.remote.model.RemoteResult
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import java.time.LocalDateTime

class MusicPlaylistRemoteDataSourceImpl(
    private val client: HttpClient
): MusicPlaylistRemoteDataSource {
    override suspend fun fetchMusicPlaylistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<MusicPlaylist>> {
        val result: RemoteResult<List<RemoteMusicPlaylist>> = client.safeRequest {
            get(
                urlString = ServerRoutes.MusicPlaylist.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.toSoulResult(
            mapData = { songs ->
                songs.map { it.toMusicPlaylist() }
            }
        )
    }
}