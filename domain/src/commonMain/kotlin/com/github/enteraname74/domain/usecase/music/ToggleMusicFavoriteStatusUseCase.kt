package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class ToggleMusicFavoriteStatusUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val musicRepository: MusicRepository,
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val music: Music = musicRepository.getFromId(musicId).first() ?: return
        val favoritePlaylist: PlaylistWithMusics = getFavoritePlaylistWithMusicsUseCase(
            dataMode = music.dataMode,
        ).first() ?: return

        if (isMusicInFavoritePlaylistUseCase(musicId = musicId).first()) {
            musicPlaylistRepository.delete(
                musicPlaylist = MusicPlaylist(
                    musicId = musicId,
                    playlistId = favoritePlaylist.playlist.playlistId,
                    dataMode = music.dataMode,
                ),
            )
        } else {
            musicPlaylistRepository.upsert(
                MusicPlaylist(
                    musicId = musicId,
                    playlistId = favoritePlaylist.playlist.playlistId,
                    dataMode = music.dataMode,
                )
            )
        }
    }
}