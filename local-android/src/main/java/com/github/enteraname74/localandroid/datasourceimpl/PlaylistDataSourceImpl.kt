package com.github.enteraname74.localandroid.datasourceimpl

import com.github.enteraname74.localandroid.AppDatabase
import com.github.enteraname74.localandroid.model.toPlaylist
import com.github.enteraname74.localandroid.model.toPlaylistWIthMusics
import com.github.enteraname74.localandroid.model.toRoomPlaylist
import com.github.enteraname74.datasource.PlaylistDataSource
import com.github.enteraname74.model.Playlist
import com.github.enteraname74.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

internal class PlaylistDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : PlaylistDataSource {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao.insertPlaylist(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao.deletePlaylist(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>> {
        return appDatabase.playlistDao.getAllPlaylistsSortByNameAscAsFlow().map { list ->
            list.map { it.toPlaylist() }
        }
    }

    override fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistsWithMusicsSortByNameAscAsFlow().map { list ->
            list.map { it.toPlaylistWIthMusics() }
        }
    }

    override fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusicsSortByNameDescAsFlow().map { list ->
            list.map { it.toPlaylistWIthMusics() }
        }
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusicsSortByAddedDateAscAsFlow()
            .map { list ->
                list.map { it.toPlaylistWIthMusics() }
            }
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusicsSortByAddedDateDescAsFlow()
            .map { list ->
                list.map { it.toPlaylistWIthMusics() }
            }
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow()
            .map { list ->
                list.map { it.toPlaylistWIthMusics() }
            }
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow()
            .map { list ->
                list.map { it.toPlaylistWIthMusics() }
            }
    }

    override fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistsFromQuickAccessAsFlow().map { list ->
            list.map { it.toPlaylistWIthMusics() }
        }
    }

    override suspend fun getFavoritePlaylist(): Playlist {
        return appDatabase.playlistDao.getFavoritePlaylist().toPlaylist()
    }

    override suspend fun getPlaylistFromId(playlistId: UUID): Playlist {
        return appDatabase.playlistDao.getPlaylistFromId(
            playlistId = playlistId
        ).toPlaylist()
    }

    override fun getPlaylistWithMusicsAsFlow(playlistId: UUID): Flow<PlaylistWithMusics?> {
        return appDatabase.playlistDao.getPlaylistWithMusicsAsFlow(playlistId = playlistId)
            .map { it?.toPlaylistWIthMusics() }
    }

    override suspend fun getAllPlaylistsWithMusics(): List<PlaylistWithMusics> {
        return appDatabase.playlistDao.getAllPlaylistsWithMusics().map { it.toPlaylistWIthMusics() }
    }

    override suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID): Int {
        return appDatabase.playlistDao.getNumberOfPlaylistsWithCoverId(
            coverId = coverId
        )
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID) {
        appDatabase.playlistDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            playlistId = playlistId
        )
    }

    override suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int {
        return appDatabase.playlistDao.getNbPlayedOfPlaylist(
            playlistId = playlistId
        )
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID) {
        appDatabase.playlistDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            playlistId = playlistId
        )
    }
}