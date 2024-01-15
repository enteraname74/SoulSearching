package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.localdesktop.dao.MusicAlbumDao
import java.util.UUID

/**
 * Implementation of the MusicAlbumDao for Exposed.
 */
class ExposedMusicAlbumDaoImpl: MusicAlbumDao {
    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromAlbum(musicId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int {
        TODO("Not yet implemented")
    }
}