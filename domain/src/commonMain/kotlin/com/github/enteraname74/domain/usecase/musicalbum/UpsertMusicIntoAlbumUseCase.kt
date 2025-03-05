package com.github.enteraname74.domain.usecase.musicalbum

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.repository.MusicAlbumRepository

class UpsertMusicIntoAlbumUseCase(
    private val musicAlbumRepository: MusicAlbumRepository,
) {
    suspend operator fun invoke(musicAlbum: MusicAlbum) {
        musicAlbumRepository.upsertMusicIntoAlbum(
            musicAlbum = musicAlbum,
        )
    }
}