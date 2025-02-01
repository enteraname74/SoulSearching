package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdatePlaylistNbPlayedUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: UUID): SoulResult<Unit> {
        val playlist: Playlist = playlistRepository.getFromId(playlistId).first() ?: return SoulResult.ofSuccess()
        return playlistRepository.update(
            playlist.copy(
                nbPlayed = playlist.nbPlayed + 1
            )
        )
    }
}