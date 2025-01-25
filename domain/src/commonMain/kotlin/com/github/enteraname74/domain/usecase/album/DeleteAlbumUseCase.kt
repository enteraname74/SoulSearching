package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class DeleteAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    suspend operator fun invoke(albumId: UUID): SoulResult<String> {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first()
            ?: return SoulResult.Success("")

        /*
        Album may hold songs that are shared by other artists.
        These songs will be deleted, but we must fetch all the related artists to check if we can delete them afterward.
        (if they are empty).
         */
        val linkedArtists: List<Artist> = buildList {
            albumWithMusics.musics.forEach { music ->
                getArtistsOfMusicUseCase(musicId = music.musicId).firstOrNull()?.let {
                    addAll(it)
                }
            }
        }
            .filter { it.artistId != albumWithMusics.artist?.artistId }
            .distinctBy { it.artistId }

        val musicDeletionResult: SoulResult<String> = musicRepository.deleteAll(
            ids = albumWithMusics.musics.map { it.musicId },
        )

        if (musicDeletionResult.isError()) return musicDeletionResult

        // We then delete the album
        val albumDeletionResult: SoulResult<String> = albumRepository.delete(albumWithMusics.album)
        if (albumDeletionResult.isError()) return albumDeletionResult

        // Finally we can check if we can delete the artist of the deleted album.
        albumWithMusics.artist?.let {
            val artistDeletionResult = deleteArtistIfEmptyUseCase(it.artistId)
            if (artistDeletionResult.isError()) return artistDeletionResult
        }

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            val artistDeletionResult = deleteArtistIfEmptyUseCase(it.artistId)
            if (artistDeletionResult.isError()) return artistDeletionResult
        }

        return SoulResult.Success("")
    }

    suspend fun onlyAlbum(albumId: UUID): SoulResult<String> =
        albumRepository.getAlbumWithMusics(albumId = albumId).firstOrNull()?.let {
            albumRepository.delete(it.album)
        } ?: SoulResult.Success("")
}