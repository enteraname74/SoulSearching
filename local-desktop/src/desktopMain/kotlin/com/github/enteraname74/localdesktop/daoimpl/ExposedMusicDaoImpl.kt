package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.localdesktop.dao.MusicDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the MusicDao for Exposed.
 */
class ExposedMusicDaoImpl: MusicDao {
    override suspend fun insertMusic(music: Music) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusic(music: Music) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicFromPath(musicPath: String): Music? {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicFromId(musicId: UUID): Music {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMusicsPaths(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getNbPlayedOfMusic(musicId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicsFromFolder(folderName: String): List<Music> {
        TODO("Not yet implemented")
    }
}