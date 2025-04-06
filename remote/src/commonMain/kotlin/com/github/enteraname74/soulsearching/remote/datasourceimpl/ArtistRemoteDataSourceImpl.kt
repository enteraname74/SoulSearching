package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.httpclient.safeRequest
import com.github.enteraname74.soulsearching.features.httpclient.safeSimpleRequest
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.artist.RemoteArtist
import com.github.enteraname74.soulsearching.remote.model.artist.toModifiedArtist
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.*

class ArtistRemoteDataSourceImpl(
    private val client: HttpClient
) : ArtistRemoteDataSource {
    override suspend fun checkForDeletedArtists(artistIds: List<UUID>): SoulResult<List<UUID>> {
        val result: SoulResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Artist.CHECK) {
                setBody(
                    artistIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.map { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchArtistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<Artist>> {
        val result: SoulResult<List<RemoteArtist>> = client.safeRequest {
            get(
                urlString = ServerRoutes.Artist.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.map { songs ->
            songs.map { it.toArtist() }
        }
    }

    override suspend fun deleteAll(artistIds: List<UUID>): SoulResult<Unit> =
        client.safeRequest<String> {
            delete(urlString = ServerRoutes.Artist.DELETE) {
                setBody(artistIds.map { it.toString() })
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()

    override suspend fun update(artist: Artist): SoulResult<Unit> =
        client.safeSimpleRequest {
            put(urlString = ServerRoutes.Artist.UPDATE) {
                setBody(artist.toModifiedArtist())
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()
}