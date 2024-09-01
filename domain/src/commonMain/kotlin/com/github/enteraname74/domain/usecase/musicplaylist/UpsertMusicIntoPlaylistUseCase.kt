package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository

class UpsertMusicIntoPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository
) {
    suspend operator fun invoke(musicPlaylist: MusicPlaylist) {
        musicPlaylistRepository.upsertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist,
        )
    }
}