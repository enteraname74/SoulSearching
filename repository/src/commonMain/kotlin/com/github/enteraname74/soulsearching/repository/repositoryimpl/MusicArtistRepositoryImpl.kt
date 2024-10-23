package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import java.util.*

/**
 * Repository of a MusicArtist.
 */
class MusicArtistRepositoryImpl(
    private val musicArtistDataSource: MusicArtistDataSource
): MusicArtistRepository {
    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistDataSource.upsertMusicIntoArtist(
            musicArtist = musicArtist
        )

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDataSource.upsertAll(musicArtists)
    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) =
        musicArtistDataSource.updateArtistOfMusic(
            musicId = musicId,
            newArtistId = newArtistId
        )

    override suspend fun deleteMusicFromArtist(musicId: UUID) = musicArtistDataSource.deleteMusicFromArtist(
        musicId = musicId
    )

    override suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? =
        musicArtistDataSource.getArtistIdFromMusicId(
            musicId = musicId
        )
}