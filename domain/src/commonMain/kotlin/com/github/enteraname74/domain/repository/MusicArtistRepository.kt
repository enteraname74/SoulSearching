package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.SoulResult
import java.util.*

interface MusicArtistRepository {
    suspend fun getAll(dataMode: DataMode): List<MusicArtist>

    suspend fun get(artistId: UUID, musicId: UUID): MusicArtist?

    suspend fun deleteAll(dataMode: DataMode)
    suspend fun delete(musicArtist: MusicArtist)
    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist)

    suspend fun upsertAll(musicArtists: List<MusicArtist>)

    /**
     * Synchronize remote MusicArtist links of the users with the cloud
     */
    suspend fun syncWithCloud(): SoulResult<Unit>
}