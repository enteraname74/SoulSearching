package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdatePlaylistNbPlayedUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: UUID) {
        val playlist: Playlist = playlistRepository.getFromId(playlistId).first() ?: return
        playlistRepository.update(
            playlist.copy(
                nbPlayed = playlist.nbPlayed + 1
            )
        )
    }
}