package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class GetSelectablePlaylistWithMusicsForMusicUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(musicId: UUID): List<PlaylistWithMusics> =
        playlistRepository
            .getAllPlaylistWithMusics()
            .first()
            .filter { playlistWithMusics ->
                playlistWithMusics.musics.none { it.musicId == musicId }
            }
}