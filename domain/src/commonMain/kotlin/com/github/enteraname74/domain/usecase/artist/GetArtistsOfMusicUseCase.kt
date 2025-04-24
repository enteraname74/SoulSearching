package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetArtistsOfMusicUseCase(
    private val artistRepository: ArtistRepository,
) {
    operator fun invoke(
        music: Music,
        withAlbumArtist: Boolean = false,
    ): Flow<List<Artist>> =
        artistRepository.getArtistsOfMusic(
            music = music,
            withAlbumArtist = withAlbumArtist,
        )
}