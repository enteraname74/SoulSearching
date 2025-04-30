package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class DeleteAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
) {
    suspend operator fun invoke(albumId: UUID) {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first() ?: return

        /*
        Album may hold songs that are shared by other artists.
        These songs will be deleted, but we must fetch all the related artists to check if we can delete them afterward.
        (if they are empty).
         */
        val linkedArtists: List<Artist> = buildList {
            albumWithMusics.musics.forEach { music ->
                getArtistsOfMusicUseCase(
                    music = music,
                    withAlbumArtist = true,
                ).firstOrNull()?.let {
                    addAll(it)
                }
            }
        }
            .filter { it.artistId != albumWithMusics.artist?.artistId }
            .distinctBy { it.artistId }

        // We first delete the musics of the album.
        albumWithMusics.musics.forEach { music ->
            musicRepository.delete(music)
        }
        // We then delete the album
        albumRepository.delete(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        albumWithMusics.artist?.let {
            commonArtistUseCase.deleteIfEmpty(
                artistId = it.artistId,
            )
        }

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            commonArtistUseCase.deleteIfEmpty(it.artistId)
        }
    }

    suspend fun onlyAlbum(albumId: UUID) {
        albumRepository.getAlbumWithMusics(albumId = albumId).firstOrNull()?.let {
            albumRepository.delete(it.album)
        }
    }
}