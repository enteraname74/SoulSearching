package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class IsMusicInFavoritePlaylistUseCase(
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
) {
    suspend operator fun invoke(musicId: UUID): Boolean =
        getFavoritePlaylistWithMusicsUseCase()
            .first()?.musics?.any { it.musicId == musicId } ?: false
}