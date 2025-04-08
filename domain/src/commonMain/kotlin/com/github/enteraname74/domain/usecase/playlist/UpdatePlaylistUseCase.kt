package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import java.util.UUID

class UpdatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(
        playlist: Playlist,
        newCoverId: UUID? = null,
    ): SoulResult<Unit> =
        playlistRepository.update(
            playlist = playlist,
            newCoverId = newCoverId,
        )
}