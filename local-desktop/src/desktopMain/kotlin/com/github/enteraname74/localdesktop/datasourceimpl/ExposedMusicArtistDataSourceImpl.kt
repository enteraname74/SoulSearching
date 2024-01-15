package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdesktop.dao.MusicArtistDao
import java.util.UUID

/**
 * Implementation of the MusicArtistDataSource with Exposed.
 */
internal class ExposedMusicArtistDataSourceImpl(
    private val musicArtistDao: MusicArtistDao
): MusicArtistDataSource {
    override suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistDao.insertMusicIntoArtist(musicArtist = musicArtist)

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) =
        musicArtistDao.updateArtistOfMusic(
            musicId = musicId,
            newArtistId = newArtistId
        )

    override suspend fun deleteMusicFromArtist(musicId: UUID) =
        musicArtistDao.deleteMusicFromArtist(musicId = musicId)

    override suspend fun getArtistIdFromMusicId(musicId: UUID) =
        musicArtistDao.getArtistIdFromMusicId(musicId = musicId)

    override suspend fun getNumberOfMusicsFromArtist(artistId: UUID) =
        musicArtistDao.getNumberOfMusicsFromArtist(artistId = artistId)
}