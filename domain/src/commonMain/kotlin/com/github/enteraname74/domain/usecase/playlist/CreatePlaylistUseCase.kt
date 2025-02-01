package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlist: Playlist): SoulResult<Playlist> =
        playlistRepository.create(
            playlist = playlist,
        )
}