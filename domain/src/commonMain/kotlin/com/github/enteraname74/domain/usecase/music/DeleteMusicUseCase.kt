package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetCorrespondingArtistUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class DeleteMusicUseCase(
    private val musicRepository: MusicRepository,
    private val getCorrespondingArtistUseCase: GetCorrespondingArtistUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val musicToRemove: Music = musicRepository.getFromId(musicId = musicId).first() ?: return
        musicRepository.delete(music = musicToRemove)

        val artist: Artist? = getCorrespondingArtistUseCase(musicId = musicToRemove.musicId)
        val album: Album? = getCorrespondingAlbumUseCase(musicId = musicToRemove.musicId)
        album?.let { musicAlbum ->
            deleteAlbumIfEmptyUseCase(albumId = musicAlbum.albumId)
        }
        artist?.let { musicArtist ->
            deleteArtistIfEmptyUseCase(artistId = musicArtist.artistId)
        }
    }

    suspend operator fun invoke(music: Music) {
        musicRepository.delete(music = music)
    }
}