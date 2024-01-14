package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.PlaylistDataSource
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ExposedPlaylistDataSourceImpl: PlaylistDataSource {
    override suspend fun insertPlaylist(playlist: Playlist) {

    }

    override suspend fun deletePlaylist(playlist: Playlist) {

    }

    override fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getFavoritePlaylist(): Playlist {
        return Playlist()
    }

    override suspend fun getPlaylistFromId(playlistId: UUID): Playlist {
        return Playlist()
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