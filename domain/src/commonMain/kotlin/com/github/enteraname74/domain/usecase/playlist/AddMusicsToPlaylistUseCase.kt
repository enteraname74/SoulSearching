package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import java.util.UUID

class AddMusicsToPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(
        playlistId: UUID,
        musicIds: List<UUID>,
    ): SoulResult<Unit> =
        playlistRepository.addMusicsToPlaylist(
            playlistId = playlistId,
            musicIds = musicIds,
        )
}