package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import kotlin.uuid.Uuid

/**
 * Repository of a MusicArtist.
 */
class MusicArtistRepositoryImpl(
    private val musicArtistDataSource: MusicArtistDataSource
): MusicArtistRepository {
    override suspend fun get(artistId: Uuid, musicId: Uuid): MusicArtist? =
        musicArtistDataSource.get(artistId, musicId)

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        musicArtistDataSource.upsertMusicIntoArtist(
            musicArtist = musicArtist,
        )
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        musicArtistDataSource.upsertAll(musicArtists)
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        musicArtistDataSource.deleteMusicArtist(musicArtist)
    }

    override suspend fun deleteOfArtist(artistId: Uuid) {
        musicArtistDataSource.deleteOfArtist(artistId)
    }

    override suspend fun deleteOfMusic(musicId: Uuid) {
        musicArtistDataSource.deleteOfMusic(musicId)
    }
}
