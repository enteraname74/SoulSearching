package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Check if an album can be deleted automatically (no songs on the album).
 * Delete the album if possible.
 */
class DeleteAlbumIfEmptyUseCase(
    private val albumRepository: AlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
) {
    suspend operator fun invoke(albumId: UUID) {
        val albumWithMusics: AlbumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first() ?: return

        println("Got album to check: $albumWithMusics")
        if (albumWithMusics.musics.isEmpty()) {
            albumRepository.delete(album = albumWithMusics.album)
            albumArtistRepository.delete(albumId = albumWithMusics.album.albumId)
        }
    }
}