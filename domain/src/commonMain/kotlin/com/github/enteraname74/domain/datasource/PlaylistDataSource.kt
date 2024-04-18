package com.github.enteraname74.domain.datasource

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Playlist.
 */
interface PlaylistDataSource {
    /**
     * Inserts or updates a Playlist.
     */
    suspend fun insertPlaylist(playlist: Playlist)

    /**
     * Deletes a Playlist.
     */
    suspend fun deletePlaylist(playlist: Playlist)

    /**
     * Retrieves a flow of all Playlist, sorted by name asc.
     */
    fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name desc.
     */
    fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by added date asc.
     */
    fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by added date desc.
     */
    fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by nb played asc.
     */
    fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted nb played desc.
     */
    fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a flow of all PlaylistWithMusics from the quick access.
     */
    fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves the favorite Playlist.
     */
    suspend fun getFavoritePlaylist(): Playlist

    /**
     * Retrieves a Playlist from its id.
     */
    suspend fun getPlaylistFromId(playlistId: UUID): Playlist

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusicsAsFlow(playlistId: UUID): Flow<PlaylistWithMusics?>

    /**
     * Retrieves all PlaylistWithMusics.
     */
    suspend fun getAllPlaylistsWithMusics(): List<PlaylistWithMusics>

    /**
     * Retrieves the number of Playlist sharing the same cover.
     */
    suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID): Int

    /**
     * Update the quick access state of a Playlist.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID)

    /**
     * Retrieves the number of time a Playlist has been played.
     */
    suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int

    /**
     * Update the total of played time of a Playlist.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID)
}