package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.model.MusicAlbum
import java.util.UUID

class ExposedMusicAlbumDataSourceImpl: MusicAlbumDataSource {
    override suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) {

    }

    override suspend fun deleteMusicFromAlbum(musicId: UUID) {

    }

    override suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) {

    }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {

    }

    override suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> {
        return emptyList()
    }

    override suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return null
    }

    override suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int {
        return 0
    }
}