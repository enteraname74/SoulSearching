package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class IsMusicInFavoritePlaylistUseCase(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
) {
    operator fun invoke(musicId: Uuid): Flow<Boolean> =
        commonPlaylistUseCase.getFavorite().map { favoritePlaylist ->
            favoritePlaylist?.musics?.any { it.musicId == musicId } ?: false
        }
}
