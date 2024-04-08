package com.github.enteraname74.domain.util

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository

/**
 * Handles the check and deletion of elements.
 */
class CheckAndDeleteVerification(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val musicArtistRepository: MusicArtistRepository
) {
    /**
     * Check if an album can be deleted automatically (no songs in the album).
     * Delete the album if possible.
     */
    suspend fun checkAndDeleteAlbum(albumToCheck: Album) {
        if (musicAlbumRepository.getNumberOfMusicsFromAlbum(albumId = albumToCheck.albumId) == 0) {
            albumRepository.deleteAlbum(album = albumToCheck)
            albumArtistRepository.deleteAlbumFromArtist(albumId = albumToCheck.albumId)
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