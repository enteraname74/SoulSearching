package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.CloudRepository
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
    private val cloudRepository: CloudRepository,
) {
    private suspend fun deleteLocal(album: Album): SoulResult<String> {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = album.albumId).first()
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

        musicRepository.deleteAll(
            ids = albumWithMusics.musics.map { it.musicId },
        )

        // We then delete the album
        albumRepository.delete(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        albumWithMusics.artist?.let {
            deleteArtistIfEmptyUseCase(it.artistId)
        }

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            deleteArtistIfEmptyUseCase(it.artistId)
        }

        return SoulResult.Success("")
    }

    suspend operator fun invoke(album: Album): SoulResult<String> =
        when(album.dataMode) {
            DataMode.Local -> deleteLocal(album)
            DataMode.Cloud -> {
                val result = albumRepository.delete(album)
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    suspend fun onlyAlbum(albumId: UUID): SoulResult<String> =
        albumRepository.getAlbumWithMusics(albumId = albumId).firstOrNull()?.let {
            albumRepository.delete(it.album)
        } ?: SoulResult.Success("")
}