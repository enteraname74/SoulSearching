package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.MusicArtist
import java.util.*

interface MusicArtistRepository {
    suspend fun getAll(): List<MusicArtist>

    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist)

    suspend fun upsertAll(musicArtists: List<MusicArtist>)

    suspend fun deleteMusicArtist(musicArtist: MusicArtist)
}