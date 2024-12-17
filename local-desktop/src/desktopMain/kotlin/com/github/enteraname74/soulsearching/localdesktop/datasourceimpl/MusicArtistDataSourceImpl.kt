package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import java.util.*


internal class MusicArtistDataSourceImpl(
    private val musicArtistDao: MusicArtistDao
): MusicArtistDataSource {
    override suspend fun getAll(): List<MusicArtist> =
        musicArtistDao.getAll()

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistDao.upsertMusicIntoArtist(musicArtist = musicArtist)

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDao.upsertAll(
            musicArtists = musicArtists,
        )
    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) =
        musicArtistDao.updateArtistOfMusic(
            musicId = musicId,
            newArtistId = newArtistId
        )

    override suspend fun deleteMusicFromArtist(musicId: UUID) =
        musicArtistDao.deleteMusicFromArtist(musicId = musicId)

    override suspend fun getArtistIdFromMusicId(musicId: UUID) =
        musicArtistDao.getArtistIdFromMusicId(musicId = musicId)
}