package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import kotlin.uuid.Uuid

class CommonMusicPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository
) {
    suspend fun upsert(musicPlaylist: MusicPlaylist) {
        musicPlaylistRepository.upsertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist,
        )
    }

    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistRepository.upsertAll(musicPlaylists)
    }

    suspend fun delete(
        musicId: Uuid,
        playlistId: Uuid
    ) {
        musicPlaylistRepository.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId,
        )
    }
}
