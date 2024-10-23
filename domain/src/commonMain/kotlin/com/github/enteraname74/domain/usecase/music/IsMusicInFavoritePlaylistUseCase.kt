package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class IsMusicInFavoritePlaylistUseCase(
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
) {
    operator fun invoke(musicId: UUID): Flow<Boolean> =
        getFavoritePlaylistWithMusicsUseCase().map { favoritePlaylist ->
            favoritePlaylist?.musics?.any { it.musicId == musicId } ?: false
        }
}