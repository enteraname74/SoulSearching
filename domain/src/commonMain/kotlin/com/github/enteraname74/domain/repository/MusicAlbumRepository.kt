package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.model.MusicAlbum
import java.util.UUID

/**
 * Repository of a MusicAlbum.
 */
class MusicAlbumRepository(
    private val musicAlbumDataSource: MusicAlbumDataSource
) {
    /**
     * Inserts or updates a MusicAlbum.
     * It is the equivalent of adding a Music to an Album.
     */
    suspend fun insertMusicIntoAlbum(musicAlbum: MusicAlbum) =
        musicAlbumDataSource.insertMusicIntoAlbum(
            musicAlbum = musicAlbum
        )

    /**
     * Deletes a MusicAlbum.
     * It is the equivalent of deleting a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(musicId: UUID) = musicAlbumDataSource.getAlbumIdFromMusicId(
        musicId = musicId
    )

    /**
     * Update the Album of a Music.
     */
    suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID) =
        musicAlbumDataSource.updateAlbumOfMusic(
            musicId = musicId,
            newAlbumId = newAlbumId
        )

    /**
     * Replace an album by an other one.
     */
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) =
        musicAlbumDataSource.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId
        )

    /**
     * Retrieve a list of ids of Music in an Album.
     */
    suspend fun getMusicsIdsFromAlbumId(albumId: UUID): List<UUID> =
        musicAlbumDataSource.getMusicsIdsFromAlbumId(
            albumId = albumId
        )

    /**
     * Tries to retrieve the album id of a Music from its id.
     */
    suspend fun getAlbumIdFromMusicId(musicId: UUID): UUID? =
        musicAlbumDataSource.getAlbumIdFromMusicId(
            musicId = musicId
        )

    /**
     * Retrieves the number of musics in an Album.
     */
    suspend fun getNumberOfMusicsFromAlbum(albumId: UUID): Int =
        musicAlbumDataSource.getNumberOfMusicsFromAlbum(
            albumId = albumId
        )
}