package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.localdesktop.dao.MusicAlbumDao
import java.util.UUID

/**
 * Implementation of the MusicAlbumDao with Exposed.
 */
internal class ExposedMusicAlbumDataSourceImpl(
    private val musicAlbumDao: MusicAlbumDao
) : MusicAlbumDataSource {
    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) =
        musicAlbumDao.insertMusicIntoAlbum(musicAlbum = musicAlbum)

    override suspend fun deleteMusicFromAlbum(musicId: UUID) =
        musicAlbumDao.deleteMusicFromAlbum(musicId = musicId)

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) =
        musicAlbumDao.updateAlbumOfMusic(
            musicId = musicId,
            newAlbumId = newAlbumId
        )

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) =
        musicAlbumDao.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId
        )

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID) =
        musicAlbumDao.getMusicsIdsFromAlbumId(albumId = albumId)

    override suspend fun getAlbumIdFromMusicId(musicId: UUID) =
        musicAlbumDao.getAlbumIdFromMusicId(musicId = musicId)

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID) =
        musicAlbumDao.getNumberOfMusicsFromAlbum(albumId = albumId)
}