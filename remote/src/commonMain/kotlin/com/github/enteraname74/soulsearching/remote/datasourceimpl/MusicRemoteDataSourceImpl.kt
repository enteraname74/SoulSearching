package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.RemoteMusic
import com.github.enteraname74.soulsearching.remote.model.RemoteResult
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*

class MusicRemoteDataSourceImpl(
    private val client: HttpClient
): MusicRemoteDataSource {
    override suspend fun fetchAllMusicOfUser(): SoulResult<List<Music>> {
        val result: RemoteResult<List<RemoteMusic>> = client.safeRequest<List<RemoteMusic>> {
            get(urlString = ServerRoutes.Music.ALL)
        }

        return result.toSoulResult(
            mapData = { songs ->
                songs.map { it.toMusic() }
            }
        )
    }
}