package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import java.util.*

class CommonMusicPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository
) {
    suspend fun upsert(musicPlaylist: MusicPlaylist) {
        musicPlaylistRepository.upsertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist,
        )
    }

    suspend fun delete(
        musicId: UUID,
        playlistId: UUID
    ) {
        musicPlaylistRepository.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId,
        )
    }
}