package com.github.enteraname74.domain.usecase.musicalbum

import com.github.enteraname74.domain.repository.MusicAlbumRepository
import java.util.UUID

class GetAlbumIdFromMusicIdUseCase(
    private val musicAlbumRepository: MusicAlbumRepository,
) {
    suspend operator fun invoke(musicId: UUID) =
        musicAlbumRepository.getAlbumIdFromMusicId(musicId)
}