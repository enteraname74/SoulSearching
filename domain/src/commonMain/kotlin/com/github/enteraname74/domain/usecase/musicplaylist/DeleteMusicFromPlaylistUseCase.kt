package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import java.util.UUID

class DeleteMusicFromPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
) {
    suspend operator fun invoke(
        musicId: UUID,
        playlistId: UUID
    ) {
        // Data mode is not important here
        musicPlaylistRepository.delete(
            musicPlaylist = MusicPlaylist(
                musicId = musicId,
                playlistId = playlistId,
                dataMode = DataMode.Local
            )
        )
    }
}