package com.github.enteraname74.localandroid.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.localandroid.AppDatabase
import com.github.enteraname74.localandroid.model.toRoomMusicAlbum
import com.github.enteraname74.domain.model.MusicAlbum
import java.util.UUID

/**
 * Implementation of the MusicAlbumDataSource with Room's DAO.
 */
internal class MusicAlbumDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicAlbumDataSource {
    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {
        appDatabase.musicAlbumDao.insertMusicIntoAlbum(
            roomMusicAlbum = musicAlbum.toRoomMusicAlbum()
        )
    }

    override suspend fun deleteMusicFromAlbum(musicId: UUID) {
        appDatabase.musicAlbumDao.deleteMusicFromAlbum(
            musicId = musicId
        )
    }

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) {
        appDatabase.musicAlbumDao.updateAlbumOfMusic(
            musicId = musicId,
            newAlbumId = newAlbumId
        )
    }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        appDatabase.musicAlbumDao.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId
        )
    }

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> {
        return appDatabase.musicAlbumDao.getMusicsIdsFromAlbumId(
            albumId = albumId
        )
    }

    override suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return appDatabase.musicAlbumDao.getAlbumIdFromMusicId(
            musicId = musicId
        )
    }

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int {
        return appDatabase.musicAlbumDao.getNumberOfMusicsFromAlbum(
            albumId = albumId
        )
    }
}