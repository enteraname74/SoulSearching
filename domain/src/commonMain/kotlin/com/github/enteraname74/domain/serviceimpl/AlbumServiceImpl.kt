package com.github.enteraname74.domain.serviceimpl

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.service.AlbumService
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import java.util.UUID

/**
 * Implementation of the AlbumService using the specified repositories.
 */
class AlbumServiceImpl(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val checkAndDeleteVerification: CheckAndDeleteVerification
) : AlbumService{
    override suspend fun delete(albumId: UUID) {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId)

        // We first delete the musics of the album.
        musicRepository.deleteMusicFromAlbum(
            album = albumWithMusics.album.albumName,
            artist = albumWithMusics.artist!!.artistName
        )
        // We then delete the album
        albumRepository.deleteAlbum(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        checkAndDeleteVerification.checkAndDeleteArtist(
            artistToCheck = albumWithMusics.artist,
        )
    }
}