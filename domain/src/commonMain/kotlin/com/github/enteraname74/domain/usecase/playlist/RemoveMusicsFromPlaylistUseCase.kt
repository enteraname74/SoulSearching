package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import java.util.UUID

class RemoveMusicsFromPlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(
        playlistId: UUID,
        musicIds: List<UUID>,
    ): SoulResult<Unit> =
        playlistRepository.removeMusicsFromPlaylist(
            playlistId = playlistId,
            musicIds = musicIds,
        )
}