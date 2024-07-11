package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first

class DeleteArtistUseCase(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artistWithMusics: ArtistWithMusics) {
        // We first delete the songs of the artist.
        for (music in artistWithMusics.musics){
            musicRepository.delete(music)
        }
        // We then delete all the albums of the artist.
        val albumsToDelete = albumRepository.getAlbumsOfArtist(
            artistId = artistWithMusics.artist.artistId
        ).first()
        for (album in albumsToDelete) {
            albumRepository.delete(album)
        }
        // And we finally delete the artist.
        artistRepository.delete(artistWithMusics.artist)
    }
}