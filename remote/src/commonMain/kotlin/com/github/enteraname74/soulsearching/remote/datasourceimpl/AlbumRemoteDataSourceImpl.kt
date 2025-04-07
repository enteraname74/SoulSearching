package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.httpclient.safeRequest
import com.github.enteraname74.soulsearching.features.httpclient.safeSimpleRequest
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.ext.appendFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.contentType
import com.github.enteraname74.soulsearching.remote.model.album.RemoteAlbum
import com.github.enteraname74.soulsearching.remote.model.album.toModifiedAlbum
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File
import java.time.LocalDateTime
import java.util.*

class AlbumRemoteDataSourceImpl(
    private val client: HttpClient
) : AlbumRemoteDataSource {
    override suspend fun checkForDeletedAlbums(albumIds: List<UUID>): SoulResult<List<UUID>> {
        val result: SoulResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Album.CHECK) {
                setBody(
                    albumIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.map { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchAlbumsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<Album>> {
        val result: SoulResult<List<RemoteAlbum>> = client.safeRequest {
            get(
                urlString = ServerRoutes.Album.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.map { songs ->
            songs.map { it.toAlbum() }
        }
    }

    override suspend fun deleteAll(albumIds: List<UUID>): SoulResult<Unit> =
        client.safeRequest<String> {
            delete(urlString = ServerRoutes.Album.DELETE) {
                setBody(albumIds.map { it.toString() })
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()

    override suspend fun update(
        album: Album,
        artist: String,
        newCover: File?,
    ): SoulResult<Unit> {
        val contentType: String = newCover?.contentType() ?: ""

        return client.safeSimpleRequest {
            submitFormWithBinaryData(
                url = ServerRoutes.Album.UPDATE,
                formData = formData {
                    newCover?.let {
                        appendFile(
                            key = "cover",
                            contentType = contentType,
                            file = it,
                        )
                    }
                    appendJson(
                        key = "metadata",
                        value = album.toModifiedAlbum(artist),
                    )
                }
            ) {
                this.method = HttpMethod.Put
            }
        }.toSimpleResult()
    }

}