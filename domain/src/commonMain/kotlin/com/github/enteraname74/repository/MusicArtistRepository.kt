package com.github.enteraname74.repository

import com.github.enteraname74.datasource.MusicArtistDataSource
import com.github.enteraname74.model.MusicArtist
import java.util.UUID

/**
 * Repository of a MusicArtist.
 */
class MusicArtistRepository(
    private val musicArtistDataSource: MusicArtistDataSource
) {
    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) =
        musicArtistDataSource.insertMusicIntoArtist(
            musicArtist = musicArtist
        )

    /**
     * Change the Artist of a Music.
     */
    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) =
        musicArtistDataSource.updateArtistOfMusic(
            musicId = musicId,
            newArtistId = newArtistId
        )

    /**
     * Deletes a MusicArtist.
     * It is the equivalent of removing a Music from an Artist.
     */
    suspend fun deleteMusicFromArtist(musicId: UUID) = musicArtistDataSource.deleteMusicFromArtist(
        musicId = musicId
    )

    /**
     * Tries to retrieves the id of an Artist of a Music from its id.
     */
    suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? =
        musicArtistDataSource.getArtistIdFromMusicId(
            musicId = musicId
        )

    /**
     * Get the number of musics from an Artist.
     */
    suspend fun getNumberOfMusicsFromArtist(artistId: UUID): Int =
        musicArtistDataSource.getNumberOfMusicsFromArtist(
            artistId = artistId
        )
}