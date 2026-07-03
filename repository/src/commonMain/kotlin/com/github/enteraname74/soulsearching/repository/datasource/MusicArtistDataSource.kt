package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.MusicArtist
import kotlin.uuid.Uuid

/**
 * Data source of a MusicArtist.
 */
interface MusicArtistDataSource {
    suspend fun get(artistId: Uuid, musicId: Uuid): MusicArtist?

    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist)

    suspend fun upsertAll(musicArtists: List<MusicArtist>)

    suspend fun deleteMusicArtist(musicArtist: MusicArtist)

    suspend fun deleteOfArtist(artistId: Uuid)

    suspend fun deleteOfMusic(musicId: Uuid)
}
