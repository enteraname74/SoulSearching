package com.github.enteraname74.soulsearching.repository.datasource.playlist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.repository.model.UploadedPlaylistResult
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

interface PlaylistRemoteDataSource {
    suspend fun checkForDeletedPlaylists(
        playlistIds: List<UUID>,
    ): SoulResult<List<UUID>>

    suspend fun upload(
        playlists: List<Playlist>
    ): SoulResult<List<UploadedPlaylistResult>>

    suspend fun create(
        playlistName: String,
    ): SoulResult<Playlist>

    suspend fun update(
        playlist: Playlist,
        newCover: File?,
    ): SoulResult<Playlist>

    suspend fun deleteAll(
        playlistIds: List<UUID>,
    ): SoulResult<Unit>

    suspend fun fetchPlaylistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Playlist>>

    /**
     * Add multiple musics to a playlist.
     * @return the added links between songs and playlist.
     */
    suspend fun addMusicsToPlaylist(
        playlistId: UUID,
        musicIds: List<UUID>
    ): SoulResult<List<MusicPlaylist>>

    /**
     * Remove multiple musics from a playlist.
     * @return the links to delete locally.
     */
    suspend fun deleteMusicsFromPlaylist(
        playlistId: UUID,
        musicIds: List<UUID>
    ): SoulResult<List<MusicPlaylist>>
}