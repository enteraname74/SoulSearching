package com.github.enteraname74.repository

import com.github.enteraname74.datasource.PlaylistDataSource
import com.github.enteraname74.model.Playlist
import com.github.enteraname74.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of a Playlist.
 */
class PlaylistRepository(
    private val playlistDataSource: PlaylistDataSource
) {
    /**
     * Inserts or updates a Playlist.
     */
    suspend fun insertPlaylist(playlist: Playlist) = playlistDataSource.insertPlaylist(
        playlist = playlist
    )

    /**
     * Deletes a Playlist.
     */
    suspend fun deletePlaylist(playlist: Playlist) = playlistDataSource.deletePlaylist(
        playlist = playlist
    )

    /**
     * Retrieves a flow of all Playlist, sorted by name asc.
     */
    fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>> =
        playlistDataSource.getAllPlaylistsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistsWithMusicsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name desc.
     */
    fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by added date asc.
     */
    fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by added date desc.
     */
    fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by nb played asc.
     */
    fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted nb played desc.
     */
    fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow()

    /**
     * Retrieves a flow of all PlaylistWithMusics from the quick access.
     */
    fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistsFromQuickAccessAsFlow()

    /**
     * Retrieves the favorite Playlist.
     */
    suspend fun getFavoritePlaylist(): Playlist = playlistDataSource.getFavoritePlaylist()

    /**
     * Retrieves a Playlist from its id.
     */
    suspend fun getPlaylistFromId(playlistId: UUID): Playlist =
        playlistDataSource.getPlaylistFromId(
            playlistId = playlistId
        )

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusicsAsFlow(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistDataSource.getPlaylistWithMusicsAsFlow(
            playlistId = playlistId
        )

    /**
     * Retrieves all PlaylistWithMusics.
     */
    suspend fun getAllPlaylistsWithMusics(): List<PlaylistWithMusics> =
        playlistDataSource.getAllPlaylistsWithMusics()

    /**
     * Retrieves the number of Playlist sharing the same cover.
     */
    suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID): Int =
        playlistDataSource.getNumberOfPlaylistsWithCoverId(
            coverId = coverId
        )

    /**
     * Update the quick access state of a Playlist.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID) =
        playlistDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            playlistId = playlistId
        )

    /**
     * Retrieves the number of time a Playlist has been played.
     */
    suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int =
        playlistDataSource.getNbPlayedOfPlaylist(
            playlistId = playlistId
        )

    /**
     * Update the total of played time of a Playlist.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID) =
        playlistDataSource.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            playlistId = playlistId
        )
}