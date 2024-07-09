package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first

class GetAlbumsNameFromSearchString(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(searchString: String): List<String> {
        if (searchString.isBlank()) return emptyList()

        val allAlbums = albumRepository.getAll().first()

        return allAlbums.filter { it.albumName.contains(searchString) }.map { it.albumName }
    }
}