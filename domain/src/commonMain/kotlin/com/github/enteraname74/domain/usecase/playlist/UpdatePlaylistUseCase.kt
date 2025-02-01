package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.PlaylistRepository

class UpdatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlist: Playlist) {
        playlistRepository.update(playlist)
    }
}