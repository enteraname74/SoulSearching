package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.usecase.music.DeleteAllMusicsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DeleteArtistUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val deleteAllMusicsUseCase: DeleteAllMusicsUseCase,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {

    private suspend fun deleteLocal(artistWithMusics: ArtistWithMusics): SoulResult<Unit> {
        /*
        Artist may hold songs that are shared by other artists.
        These songs will be deleted, but we must fetch all the related artists to check if we can delete them afterward.
        (if they are empty).
         */
        val linkedArtists: List<Artist> = buildList {
            artistWithMusics.musics.forEach { music ->
                getArtistsOfMusicUseCase(musicId = music.musicId).firstOrNull()?.let {
                    addAll(it)
                }
            }
        }
            .filter { it.artistId != artistWithMusics.artist.artistId }
            .distinctBy { it.artistId }


        // We first delete the songs of the artist.
        deleteAllMusicsUseCase(
            ids = artistWithMusics.musics.map { it.musicId }
        )

        // We then delete all the albums of the artist.
        val albumsToDelete = albumRepository.getAlbumsOfArtist(
            artistId = artistWithMusics.artist.artistId
        ).first()

        albumRepository.deleteAll(
            ids = albumsToDelete.map { it.albumId }
        )

        // And we finally delete the artist.
        artistRepository.delete(artistWithMusics.artist)

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            deleteArtistIfEmptyUseCase(it.artistId)
        }

        return SoulResult.ofSuccess()
    }

    suspend operator fun invoke(artistWithMusics: ArtistWithMusics): SoulResult<Unit> =
        when(artistWithMusics.artist.dataMode) {
            DataMode.Local -> deleteLocal(artistWithMusics)
            DataMode.Cloud -> artistRepository.delete(artistWithMusics.artist)
        }
}