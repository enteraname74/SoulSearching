package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import java.util.*


internal class MusicArtistLocalDataSourceImpl(
    private val musicArtistDao: MusicArtistDao
): MusicArtistLocalDataSource {
    override suspend fun getAll(dataMode: DataMode): List<MusicArtist> =
        musicArtistDao.getAll(dataMode = dataMode.value)

    override suspend fun get(artistId: UUID, musicId: UUID): MusicArtist? =
        musicArtistDao.get(
            musicId = musicId,
            artistId = artistId,
        )

    override suspend fun deleteAll(dataMode: DataMode) {
        musicArtistDao.deleteAll(dataMode.value)
    }

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