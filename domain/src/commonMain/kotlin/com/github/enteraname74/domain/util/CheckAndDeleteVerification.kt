package com.github.enteraname74.domain.util

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumRepository

/**
 * Handles the check and deletion of elements.
 */
class CheckAndDeleteVerification(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumDataSource,
    private val albumArtistRepository: AlbumArtistDataSource,
    private val musicArtistRepository: MusicArtistDataSource
) {
    /**
     * Check if an album can be deleted automatically (no songs in the album).
     * Delete the album if possible.
     */
    suspend fun checkAndDeleteAlbum(albumToCheck: Album) {
        if (musicAlbumRepository.getNumberOfMusicsFromAlbum(albumId = albumToCheck.albumId) == 0) {
            albumRepository.deleteAlbum(album = albumToCheck)
            albumArtistRepository.delete(albumId = albumToCheck.albumId)
        }
    }

    /**
     * Check if an artist can be deleted automatically (no songs in the artist).
     * Delete the artist if possible.
     */
    suspend fun checkAndDeleteArtist(artistToCheck: Artist) {
        if (musicArtistRepository.getNumberOfMusicsFromArtist(artistId = artistToCheck.artistId) == 0) {
            artistRepository.deleteArtist(artistToCheck)
        }
    }
}