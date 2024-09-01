package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomMusicAlbum
import com.github.enteraname74.soulsearching.repository.datasource.MusicAlbumDataSource
import java.util.*

/**
 * Implementation of the MusicAlbumDataSource with Room's DAO.
 */
internal class RoomMusicAlbumDataSourceImpl(
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
}