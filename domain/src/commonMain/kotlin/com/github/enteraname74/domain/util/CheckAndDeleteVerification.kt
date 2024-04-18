package com.github.enteraname74.domain.util

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.datasource.MusicArtistDataSource

/**
 * Handles the check and deletion of elements.
 */
class CheckAndDeleteVerification(
    private val albumDataSource: AlbumDataSource,
    private val artistDataSource: ArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource,
    private val musicArtistDataSource: MusicArtistDataSource
) {
    /**
     * Check if an album can be deleted automatically (no songs in the album).
     * Delete the album if possible.
     */
    suspend fun checkAndDeleteAlbum(albumToCheck: Album) {
        if (musicAlbumDataSource.getNumberOfMusicsFromAlbum(albumId = albumToCheck.albumId) == 0) {
            albumDataSource.deleteAlbum(album = albumToCheck)
            albumArtistDataSource.deleteAlbumFromArtist(albumId = albumToCheck.albumId)
        }
    }

    /**
     * Check if an artist can be deleted automatically (no songs in the artist).
     * Delete the artist if possible.
     */
    suspend fun checkAndDeleteArtist(artistToCheck: Artist) {
        if (musicArtistDataSource.getNumberOfMusicsFromArtist(artistId = artistToCheck.artistId) == 0) {
            artistDataSource.deleteArtist(artistToCheck)
        }
    }
}