package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdesktop.dao.MusicArtistDao
import java.util.UUID

/**
 * Implementation of the MusicArtistDao for Exposed.
 */
class ExposedMusicArtistDaoImpl: MusicArtistDao {
    override suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) {
        TODO("Not yet implemented")
    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromArtist(musicId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfMusicsFromArtist(artistId: UUID): Int {
        TODO("Not yet implemented")
    }
}