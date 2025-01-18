package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * Check if an album can be deleted automatically (no songs on the album).
 * Delete the album if possible.
 */
class DeleteAlbumIfEmptyUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: UUID) {
        val albumWithMusics: AlbumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first() ?: return

        if (albumWithMusics.musics.isEmpty()) {
            albumRepository.delete(album = albumWithMusics.album)
        }
    }
}