package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.MusicArtist
import java.util.UUID

/**
 * Data source of a MusicArtist.
 */
interface MusicArtistDataSource {
    suspend fun getAll(): List<MusicArtist>
    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist)

    suspend fun upsertAll(musicArtists: List<MusicArtist>)

    /**
     * Change the Artist of a Music.
     */
    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID)

    /**
     * Deletes a MusicArtist.
     * It is the equivalent of removing a Music from an Artist.
     */
    suspend fun deleteMusicFromArtist(musicId: UUID)

    /**
     * Tries to retrieve the id of an Artist of a Music from its id.
     */
    suspend fun getArtistIdFromMusicId(musicId: UUID) : UUID?
}