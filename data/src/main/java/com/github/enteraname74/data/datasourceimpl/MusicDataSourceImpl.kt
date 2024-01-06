package com.github.enteraname74.data.datasourceimpl

import com.github.enteraname74.data.AppDatabase
import com.github.enteraname74.data.model.toMusic
import com.github.enteraname74.data.model.toRoomMusic
import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of the MusicArtistDataSource with Room's DAO.
 */
internal class MusicDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : MusicDataSource {
    override suspend fun insertMusic(music: Music) {
        appDatabase.musicDao.insertMusic(
            roomMusic = music.toRoomMusic()
        )
    }

    override suspend fun deleteMusic(music: Music) {
        appDatabase.musicDao.deleteMusic(
            roomMusic = music.toRoomMusic()
        )
    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        appDatabase.musicDao.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )
    }

    override suspend fun getMusicFromPath(musicPath: String): Music? {
        return appDatabase.musicDao.getMusicFromPath(
            musicPath = musicPath
        )?.toMusic()
    }

    override suspend fun getMusicFromId(musicId: UUID): Music {
        return appDatabase.musicDao.getMusicFromId(
            musicId = musicId
        ).toMusic()
    }

    override suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? {
        return appDatabase.musicDao.getMusicFromFavoritePlaylist(
            musicId = musicId
        )?.toMusic()
    }

    override suspend fun getAllMusicsPaths(): List<String> {
        return appDatabase.musicDao.getAllMusicsPaths()
    }

    override fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByNameAscAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByNameDescAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByAddedDateAscAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByAddedDateDescAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByNbPlayedAscAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsSortByNbPlayedDescAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> {
        return appDatabase.musicDao.getAllMusicsFromQuickAccessAsFlow().map { list ->
            list.map { it.toMusic() }
        }
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> {
        return appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }
    }

    override suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int {
        return appDatabase.musicDao.getNumberOfMusicsWithCoverId(
            coverId = coverId
        )
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) {
        appDatabase.musicDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            musicId = musicId
        )
    }

    override suspend fun getNbPlayedOfMusic(musicId: UUID): Int {
        return appDatabase.musicDao.getNbPlayedOfMusic(
            musicId = musicId
        )
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) {
        appDatabase.musicDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            musicId = musicId
        )
    }

    override suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) {
        appDatabase.musicDao.updateMusicsHiddenState(
            folderName = folderName,
            newIsHidden = newIsHidden
        )
    }

    override suspend fun getMusicsFromFolder(folderName: String): List<Music> {
        return appDatabase.musicDao.getMusicsFromFolder(
            folderName = folderName
        ).map { it.toMusic() }
    }
}