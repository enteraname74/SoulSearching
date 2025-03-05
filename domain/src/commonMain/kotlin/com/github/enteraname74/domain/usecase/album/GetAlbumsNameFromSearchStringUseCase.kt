package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first

class GetAlbumsNameFromSearchStringUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(searchString: String): List<String> =
        if (searchString.isBlank()) {
            emptyList()
        } else {
            albumRepository.getAlbumNamesContainingSearch(searchString)
        }
}