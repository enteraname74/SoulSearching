package com.github.enteraname74.soulsearching.repository.datasource.musicartist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import java.util.UUID

/**
 * Data source of a MusicArtist.
 */
interface MusicArtistLocalDataSource {
    suspend fun getAll(dataMode: DataMode): List<MusicArtist>

    suspend fun get(artistId: UUID, musicId: UUID): MusicArtist?

    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Inserts or updates a MusicArtist.
     * It is the equivalent of adding a Music to an Artist.
     */
    suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist)

    suspend fun upsertAll(musicArtists: List<MusicArtist>)

    suspend fun deleteMusicArtist(musicArtist: MusicArtist)
}