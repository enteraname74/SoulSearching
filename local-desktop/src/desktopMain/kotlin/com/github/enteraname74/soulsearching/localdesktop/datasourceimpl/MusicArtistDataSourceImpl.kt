package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import java.util.*


internal class MusicArtistDataSourceImpl(
    private val musicArtistDao: MusicArtistDao
): MusicArtistDataSource {
    override suspend fun get(artistId: UUID, musicId: UUID): MusicArtist? =
        musicArtistDao.get(
            musicId = musicId,
            artistId = artistId,
        )

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistDao.upsertMusicIntoArtist(musicArtist = musicArtist)

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDao.upsertAll(
            musicArtists = musicArtists,
        )
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        musicArtistDao.delete(musicArtist)
    }
}