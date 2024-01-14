package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.model.MusicArtist
import java.util.UUID

class ExposedMusicArtistDataSourceImpl: MusicArtistDataSource {
    override suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) {

    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) {

    }

    override suspend fun deleteMusicFromArtist(musicId: UUID) {

    }

    override suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        return null
    }

    override suspend fun getNumberOfMusicsFromArtist(artistId: UUID): Int {
        return 0
    }
}