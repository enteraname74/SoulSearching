package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.RemoteMusic
import com.github.enteraname74.soulsearching.remote.model.RemoteResult
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.*

class MusicRemoteDataSourceImpl(
    private val client: HttpClient
): MusicRemoteDataSource {

    override suspend fun checkForDeletedSongs(musicIds: List<UUID>): SoulResult<List<UUID>> {
        val result: RemoteResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Music.CHECK) {
                setBody(
                    musicIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.toSoulResult { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>> {
        val result: RemoteResult<List<RemoteMusic>> = client.safeRequest<List<RemoteMusic>> {
            get(
                urlString = ServerRoutes.Music.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.toSoulResult(
            mapData = { songs ->
                songs.map { it.toMusic() }
            }
        )
    }
}