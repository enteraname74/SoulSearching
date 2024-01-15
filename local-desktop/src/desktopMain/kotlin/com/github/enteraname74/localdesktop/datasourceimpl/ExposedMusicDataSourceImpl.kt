package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.localdesktop.dao.MusicDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the MusicDataSource with Exposed.
 */
internal class ExposedMusicDataSourceImpl(
    private val musicDao: MusicDao
): MusicDataSource {
    override suspend fun insertMusic(music: Music) = musicDao.insertMusic(music = music)

    override suspend fun deleteMusic(music: Music) = musicDao.deleteMusic(music = music)

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) =
        musicDao.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )

    override suspend fun getMusicFromPath(musicPath: String) =
        musicDao.getMusicFromPath(musicPath = musicPath)

    override suspend fun getMusicFromId(musicId: UUID) =
        musicDao.getMusicFromId(musicId = musicId)

    override suspend fun getMusicFromFavoritePlaylist(musicId: UUID) =
        musicDao.getMusicFromFavoritePlaylist(musicId = musicId)

    override suspend fun getAllMusicsPaths() = musicDao.getAllMusicsPaths()

    override fun getAllMusicsSortByNameAscAsFlow() =
        musicDao.getAllMusicsSortByNameAscAsFlow()

    override fun getAllMusicsSortByNameDescAsFlow() =
        musicDao.getAllMusicsSortByNameDescAsFlow()

    override fun getAllMusicsSortByAddedDateAscAsFlow() =
        musicDao.getAllMusicsSortByAddedDateAscAsFlow()

    override fun getAllMusicsSortByAddedDateDescAsFlow() =
        musicDao.getAllMusicsSortByAddedDateDescAsFlow()

    override fun getAllMusicsSortByNbPlayedAscAsFlow() =
        musicDao.getAllMusicsSortByNbPlayedAscAsFlow()

    override fun getAllMusicsSortByNbPlayedDescAsFlow() =
        musicDao.getAllMusicsSortByNbPlayedDescAsFlow()

    override fun getAllMusicsFromQuickAccessAsFlow() =
        musicDao.getAllMusicsFromQuickAccessAsFlow()

    override suspend fun getAllMusicFromAlbum(albumId: UUID) =
        musicDao.getAllMusicFromAlbum(albumId =albumId)

    override suspend fun getNumberOfMusicsWithCoverId(coverId: UUID) =
        musicDao.getNumberOfMusicsWithCoverId(coverId = coverId)

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) =
        musicDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            musicId = musicId
        )

    override suspend fun getNbPlayedOfMusic(musicId: UUID) =
        musicDao.getNbPlayedOfMusic(musicId = musicId)

    override suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) =
        musicDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            musicId = musicId
        )

    override suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) =
        musicDao.updateMusicsHiddenState(
            folderName = folderName,
            newIsHidden = newIsHidden
        )

    override suspend fun getMusicsFromFolder(folderName: String) =
        musicDao.getMusicsFromFolder(folderName = folderName)
}