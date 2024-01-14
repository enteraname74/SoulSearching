package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ExposedMusicDataSourceImpl: MusicDataSource {
    override suspend fun insertMusic(music: Music) {

    }

    override suspend fun deleteMusic(music: Music) {

    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {

    }

    override suspend fun getMusicFromPath(musicPath: String): Music? {
        return null
    }

    override suspend fun getMusicFromId(musicId: UUID): Music {
        return Music()
    }

    override suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? {
        return null
    }

    override suspend fun getAllMusicsPaths(): List<String> {
        return emptyList()
    }

    override fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> {
        return emptyList()
    }

    override suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int {
        return 0
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) {

    }

    override suspend fun getNbPlayedOfMusic(musicId: UUID): Int {
        return 0
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) {

    }

    override suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) {

    }

    override suspend fun getMusicsFromFolder(folderName: String): List<Music> {
        return emptyList()
    }
}