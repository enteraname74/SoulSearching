package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class ToggleMusicFavoriteStatusUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val favoritePlaylist: PlaylistWithMusics = getFavoritePlaylistWithMusicsUseCase().first() ?: return

        if (isMusicInFavoritePlaylistUseCase(musicId = musicId).first()) {
            musicPlaylistRepository.deleteMusicFromPlaylist(
                musicId = musicId,
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