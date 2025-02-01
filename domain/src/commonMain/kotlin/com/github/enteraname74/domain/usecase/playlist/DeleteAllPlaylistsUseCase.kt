package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import java.util.UUID

class DeleteAllPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistIds: List<UUID>): SoulResult<Unit> =
        playlistRepository.deleteAll(playlistIds)
}