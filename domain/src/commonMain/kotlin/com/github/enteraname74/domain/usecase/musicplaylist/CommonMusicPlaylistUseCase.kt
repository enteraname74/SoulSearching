package com.github.enteraname74.domain.usecase.musicplaylist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult
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

    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>, keepUpdatedAt: Boolean = false) {
        musicPlaylistRepository.upsertAll(musicPlaylists, keepUpdatedAt)
    }

    suspend fun delete(
        musicIds: List<Uuid>,
        playlistId: Uuid
    ) {
        musicPlaylistRepository.deleteFromPlaylist(
            musicIds = musicIds,
            playlistId = playlistId,
        )
    }
}
