package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.UUID

class IsMusicInFavoritePlaylistUseCase(
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
    private val getMusicUseCase: GetMusicUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(musicId: UUID): Flow<Boolean> =
        getMusicUseCase(musicId).flatMapLatest { music ->
            music?.let {
                getFavoritePlaylistWithMusicsUseCase(music.dataMode).map { favoritePlaylist ->
                    favoritePlaylist?.musics?.any { it.musicId == musicId } ?: false
                }
            } ?: flowOf(false)
        }
}