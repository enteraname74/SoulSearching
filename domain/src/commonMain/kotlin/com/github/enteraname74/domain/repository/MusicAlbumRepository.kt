package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.MusicAlbum
import java.util.*

interface MusicAlbumRepository {
    suspend fun getAll(): List<MusicAlbum>
    /**
     * Inserts or updates a MusicAlbum.
     * It is the equivalent of adding a Music to an Album.
     */
    suspend fun upsertMusicIntoAlbum(musicAlbum: MusicAlbum)

    suspend fun upsertAll(musicAlbums: List<MusicAlbum>)

    /**
     * Deletes a MusicAlbum.
     * It is the equivalent of deleting a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(musicId: UUID)

    /**
     * Update the Album of a Music.
     */
    suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID)

    /**
     * Replace an album by another one.
     */
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    /**
     * Retrieve a list of ids of Music on an Album.
     */
    suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID>

    /**
     * Tries to retrieve the album id of a Music from its id.
     */
    suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID?
}