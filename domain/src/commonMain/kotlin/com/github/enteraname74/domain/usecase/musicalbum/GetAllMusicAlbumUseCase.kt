package com.github.enteraname74.domain.usecase.musicalbum

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.repository.MusicAlbumRepository

class GetAllMusicAlbumUseCase(
    private val musicAlbumRepository: MusicAlbumRepository
) {
    suspend operator fun invoke(): List<MusicAlbum> =
        musicAlbumRepository.getAll()
}