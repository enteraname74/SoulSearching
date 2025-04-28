package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetArtistsOfMusicUseCase(
    private val artistRepository: ArtistRepository,
) {

    /**
     * Retrieves all artists of a music.
     * If [withAlbumArtist] is set to false, the returned artists will not contain the album artist of the music.
     * However, If there is only one artist, we will keep the album artist as its main artist.
     */
    operator fun invoke(
        music: Music,
        withAlbumArtist: Boolean = false,
    ): Flow<List<Artist>> =
        artistRepository.getArtistsOfMusic(
            musicId = music.musicId,
        ).map { artists ->
            if (!withAlbumArtist && artists.size > 1) {
                artists.filter { it.artistName != music.albumArtist }
            } else {
                artists
            }
        }
}