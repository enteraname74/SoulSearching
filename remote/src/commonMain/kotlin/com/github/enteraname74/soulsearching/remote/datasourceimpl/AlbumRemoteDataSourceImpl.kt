package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.album.RemoteAlbum
import com.github.enteraname74.soulsearching.remote.model.RemoteResult
import com.github.enteraname74.soulsearching.remote.model.album.toModifiedAlbum
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.remote.model.safeSimpleRequest
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.*

class AlbumRemoteDataSourceImpl(
    private val client: HttpClient
): AlbumRemoteDataSource {
    override suspend fun checkForDeletedAlbums(albumIds: List<UUID>): SoulResult<List<UUID>> {
        val result: RemoteResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Album.CHECK) {
                setBody(
                    albumIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.toSoulResult { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchAlbumsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<Album>> {
        val result: RemoteResult<List<RemoteAlbum>> = client.safeRequest {
            get(
                urlString = ServerRoutes.Album.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.toSoulResult(
            mapData = { songs ->
                songs.map { it.toAlbum() }
            }
        )
    }

    override suspend fun deleteAll(albumIds: List<UUID>): SoulResult<Unit> =
        client.safeRequest<String> {
            delete(urlString = ServerRoutes.Album.DELETE) {
                setBody(albumIds.map { it.toString() })
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()

    override suspend fun update(album: Album, artist: String): SoulResult<Unit> =
        client.safeSimpleRequest {
            put(urlString = ServerRoutes.Album.UPDATE) {
                setBody(album.toModifiedAlbum(artist))
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()
}