package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.PlaylistDataSource
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.localdesktop.dao.PlaylistDao
import java.util.UUID

/**
 * Implementation of the PlaylistDataSource with Exposed.
 */
internal class ExposedPlaylistDataSourceImpl(
    private val playlistDao: PlaylistDao
) : PlaylistDataSource {
    override suspend fun insertPlaylist(playlist: Playlist) = playlistDao.insertPlaylist(
        playlist = playlist
    )

    override suspend fun deletePlaylist(playlist: Playlist) = playlistDao.deletePlaylist(
        playlist = playlist
    )

    override fun getAllPlaylistsSortByNameAscAsFlow() =
        playlistDao.getAllPlaylistsSortByNameAscAsFlow()

    override fun getAllPlaylistsWithMusicsSortByNameAscAsFlow() =
        playlistDao.getAllPlaylistsWithMusicsSortByNameAscAsFlow()

    override fun getAllPlaylistWithMusicsSortByNameDescAsFlow() =
        playlistDao.getAllPlaylistWithMusicsSortByNameDescAsFlow()

    override fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow() =
        playlistDao.getAllPlaylistWithMusicsSortByAddedDateAscAsFlow()

    override fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow() =
        playlistDao.getAllPlaylistWithMusicsSortByAddedDateDescAsFlow()

    override fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow() =
        playlistDao.getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow()

    override fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow() =
        playlistDao.getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow()

    override fun getAllPlaylistsFromQuickAccessAsFlow() =
        playlistDao.getAllPlaylistsFromQuickAccessAsFlow()

    override suspend fun getFavoritePlaylist() = playlistDao.getFavoritePlaylist()

    override suspend fun getPlaylistFromId(playlistId: UUID) =
        playlistDao.getPlaylistFromId(playlistId = playlistId)

    override fun getPlaylistWithMusicsAsFlow(playlistId: UUID) =
        playlistDao.getPlaylistWithMusicsAsFlow(playlistId = playlistId)

    override suspend fun getAllPlaylistsWithMusics() =
        playlistDao.getAllPlaylistsWithMusics()

    override suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID) =
        playlistDao.getNumberOfPlaylistsWithCoverId(coverId = coverId)

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID) =
        playlistDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            playlistId = playlistId
        )

    override suspend fun getNbPlayedOfPlaylist(playlistId: UUID) =
        playlistDao.getNbPlayedOfPlaylist(
            playlistId = playlistId
        )

    override suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID) =
        playlistDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            playlistId = playlistId
        )
}