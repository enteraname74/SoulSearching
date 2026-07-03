package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

class DeleteAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
) {
    suspend operator fun invoke(albumId: Uuid) {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first() ?: return

        /*
        Album may hold songs that are shared by other artists.
        These songs will be deleted, but we must fetch all the related artists to check if we can delete them afterward.
        (if they are empty).
         */
        val linkedArtists: List<Artist> = albumWithMusics.musics.flatMap { it.artists }
            .filter { it.artistId != albumWithMusics.album.artist.artistId }
            .distinctBy { it.artistId }

        // We first delete the musics of the album.
        albumWithMusics.musics.forEach { music ->
            musicRepository.delete(music)
        }
        // We then delete the album
        albumRepository.delete(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        commonArtistUseCase.deleteIfEmpty(
            artistId = albumWithMusics.album.artist.artistId,
        )

        // We delete the linked artists of songs that were deleted if they now are empty
        linkedArtists.forEach {
            commonArtistUseCase.deleteIfEmpty(it.artistId)
        }

        // We only use the DeleteMusicUseCase for the remote part, as the rest is handled in this use case
        deleteMusicUseCase.deleteRemoteIfPossible(remoteIds = albumWithMusics.musics.mapNotNull { it.remoteId })
    }

    // TODO OPTIMIZATION: Improve deletion of multiple albums?
    suspend operator fun invoke(albumIds: List<Uuid>) {
        albumIds.forEach { this(it) }
    }

    suspend fun onlyAlbum(albumId: Uuid) {
        albumRepository.getAlbumWithMusics(albumId = albumId).firstOrNull()?.let {
            albumRepository.delete(it.album)
        }
    }
}
