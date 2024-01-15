package com.github.enteraname74.localdesktop.dao

import com.github.enteraname74.domain.model.MusicArtist
import java.util.UUID

/**
 * DAO for managing MusicArtists.
 */
internal interface MusicArtistDao {
    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun insertMusicIntoArtist(musicArtist: MusicArtist)

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
     * Tries to retrieves the id of an Artist of a Music from its id.
     */
    suspend fun getArtistIdFromMusicId(musicId: UUID) : UUID?

    /**
     * Get the number of musics from an Artist.
     */
    suspend fun getNumberOfMusicsFromArtist(artistId : UUID) : Int
}