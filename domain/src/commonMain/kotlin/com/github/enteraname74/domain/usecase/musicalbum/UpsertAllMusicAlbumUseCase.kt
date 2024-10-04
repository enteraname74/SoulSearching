package com.github.enteraname74.domain.usecase.musicalbum

import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.repository.MusicAlbumRepository

class UpsertAllMusicAlbumUseCase(
    private val musicAlbumRepository: MusicAlbumRepository,
) {
    suspend operator fun invoke(allMusicAlbums: List<MusicAlbum>) {
        musicAlbumRepository.upsertAll(allMusicAlbums)
    }
}