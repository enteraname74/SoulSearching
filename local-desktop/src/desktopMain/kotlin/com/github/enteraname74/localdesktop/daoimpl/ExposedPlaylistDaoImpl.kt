package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.localdesktop.dao.PlaylistDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the PlaylistDao for Exposed.
 */
class ExposedPlaylistDaoImpl: PlaylistDao {
    override suspend fun insertPlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoritePlaylist(): Playlist {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaylistFromId(playlistId: UUID): Playlist {
        TODO("Not yet implemented")
    }

    override fun getPlaylistWithMusicsAsFlow(playlistId: UUID): Flow<PlaylistWithMusics?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPlaylistsWithMusics(): List<PlaylistWithMusics> {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID) {
        TODO("Not yet implemented")
    }
}