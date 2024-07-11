package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import java.util.UUID

class DeleteMusicFromPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
) {
    suspend operator fun invoke(
        musicId: UUID,
        playlistId: UUID
    ) {
        musicPlaylistRepository.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId,
        )
    }
}