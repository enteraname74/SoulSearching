package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AlbumRepository

class ToggleAlbumQuickAccessStateUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(album: Album): SoulResult<String> =
        albumRepository.upsert(
            album = album.copy(
                isInQuickAccess = !album.isInQuickAccess,
            )
        )
}