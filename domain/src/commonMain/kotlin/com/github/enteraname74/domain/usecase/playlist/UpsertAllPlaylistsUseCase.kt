package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.PlaylistRepository

class UpsertAllPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlists: List<Playlist>) {
        playlistRepository.upsertAll(playlists)
    }
}