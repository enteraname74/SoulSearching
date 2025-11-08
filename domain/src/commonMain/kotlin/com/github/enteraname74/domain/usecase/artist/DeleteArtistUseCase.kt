package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import kotlinx.coroutines.flow.first

class DeleteArtistUseCase(
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
) {
    suspend operator fun invoke(artistWithMusics: ArtistWithMusics) {
        /*
        Artist may hold songs that are shared by other artists.
        These songs will be deleted, but we must fetch all the related artists to check if we can delete them afterward.
        (if they are empty).
         */
        val linkedArtists: List<Artist> = artistWithMusics.musics.flatMap { it.artists }
            .filter { it.artistId != artistWithMusics.artist.artistId }
            .distinctBy { it.artistId }


        // We first delete the songs of the artist.
        commonMusicUseCase.deleteAll(
            ids = artistWithMusics.musics.map { it.musicId }
        )
        // We then delete all the albums of the artist.
        val albumsToDelete = commonAlbumUseCase.getAlbumsOfArtist(
            artistId = artistWithMusics.artist.artistId
        ).first()

        commonAlbumUseCase.deleteAll(
            albumsIds = albumsToDelete.map { it.albumId }
        )

        // And we finally delete the artist.
        commonArtistUseCase.delete(artistWithMusics.artist)

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            commonArtistUseCase.deleteIfEmpty(it.artistId)
        }
    }
}