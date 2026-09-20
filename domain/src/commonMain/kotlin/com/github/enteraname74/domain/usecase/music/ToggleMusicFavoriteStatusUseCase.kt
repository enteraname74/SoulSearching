package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class ToggleMusicFavoriteStatusUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
) {
    suspend operator fun invoke(musicId: Uuid) {
        val favoritePlaylist: PlaylistWithMusics = commonPlaylistUseCase.observeFavorite().firstOrNull() ?: return

        if (isMusicInFavoritePlaylistUseCase(musicId = musicId).first()) {
            musicPlaylistRepository.deleteFromPlaylist(
                musicIds = listOf(musicId),
                playlistId = favoritePlaylist.playlist.playlistId,
            )
        } else {
            musicPlaylistRepository.upsertMusicIntoPlaylist(
                MusicPlaylist(
                    musicId = musicId,
                    playlistId = favoritePlaylist.playlist.playlistId,
                )
            )
        }
    }
}
