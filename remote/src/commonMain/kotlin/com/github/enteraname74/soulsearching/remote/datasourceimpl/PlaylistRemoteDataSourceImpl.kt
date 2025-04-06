package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.httpclient.safeRequest
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.RemoteMusicPlaylist
import com.github.enteraname74.soulsearching.remote.model.playlist.*
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UploadedPlaylistResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.*

class PlaylistRemoteDataSourceImpl(
    private val client: HttpClient
): PlaylistRemoteDataSource {
    override suspend fun checkForDeletedPlaylists(playlistIds: List<UUID>): SoulResult<List<UUID>> {
        val result = client.safeRequest<List<String>> {
            get(urlString = ServerRoutes.Playlist.CHECK) {
                setBody(
                    playlistIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.map { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun upload(playlists: List<Playlist>): SoulResult<List<UploadedPlaylistResult>> =
        client.safeRequest<List<RemoteUploadedPlaylistData>> {
            post(urlString = ServerRoutes.Playlist.UPLOAD) {
                setBody(playlists.map { it.toPlaylistUpload() })
                contentType(ContentType.Application.Json)
            }
        }.map { list ->
            list.map { it.toUploadedPlaylistData() }
        }

    override suspend fun create(playlistName: String): SoulResult<Playlist> =
        client.safeRequest<RemotePlaylist> {
            post(urlString = ServerRoutes.Playlist.CREATE) {
                setBody(PlaylistCreation(name = playlistName))
                contentType(ContentType.Application.Json)
            }
        }.map {
            it.toPlaylist()
        }

    override suspend fun update(playlist: Playlist): SoulResult<Playlist> =
        client.safeRequest<RemotePlaylist> { 
            put(urlString = ServerRoutes.Playlist.UPDATE) {
                setBody(playlist.toModifiedPlaylist())
                contentType(ContentType.Application.Json)
            }
        }.map {
            it.toPlaylist()
        }

    override suspend fun deleteAll(playlistIds: List<UUID>): SoulResult<Unit> =
        client.safeRequest<String> {
            delete(urlString = ServerRoutes.Playlist.DELETE) {
                setBody(playlistIds.map { it.toString() })
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()

    override suspend fun fetchPlaylistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int
    ): SoulResult<List<Playlist>> {
        val result = client.safeRequest<List<RemotePlaylist>> {
            client.get(
                urlString = ServerRoutes.Playlist.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.map { list ->
            list.map { it.toPlaylist() }
        }
    }

    override suspend fun addMusicsToPlaylist(playlistId: UUID, musicIds: List<UUID>): SoulResult<List<MusicPlaylist>> =
        client.safeRequest<List<RemoteMusicPlaylist>> {
            post(
                urlString = ServerRoutes.Playlist.addMusicsToPlaylist(
                    playlistId = playlistId,
                )
            ) {
                setBody(
                    musicIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }.map { list ->
            list.map { it.toMusicPlaylist() }
        }

    override suspend fun deleteMusicsFromPlaylist(
        playlistId: UUID,
        musicIds: List<UUID>
    ): SoulResult<List<MusicPlaylist>> =
        client.safeRequest<List<RemoteMusicPlaylist>> {
            post(
                urlString = ServerRoutes.Playlist.removeMusicsFromPlaylist(
                    playlistId = playlistId,
                )
            ) {
                setBody(
                    musicIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }.map { list ->
            list.map { it.toMusicPlaylist() }
        }
}