package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicAlbumDao
import com.github.enteraname74.soulsearching.repository.datasource.MusicAlbumDataSource
import java.util.*

internal class MusicAlbumDataSourceImpl(
    private val musicAlbumDao: MusicAlbumDao
) : MusicAlbumDataSource {
    override suspend fun getAll(): List<MusicAlbum> =
        musicAlbumDao.getAll()

    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) =
        musicAlbumDao.insertMusicIntoAlbum(musicAlbum = musicAlbum)

    override suspend fun upsertAll(musicAlbums: List<MusicAlbum>) {
        musicAlbumDao.upsertAll(
            musicAlbums = musicAlbums,
        )
    }

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
}