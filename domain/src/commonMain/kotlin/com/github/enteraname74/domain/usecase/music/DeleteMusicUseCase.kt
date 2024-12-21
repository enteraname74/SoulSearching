package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class DeleteMusicUseCase(
    private val musicRepository: MusicRepository,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val musicToRemove: Music = musicRepository.getFromId(musicId = musicId).first() ?: return
        deleteMusic(music = musicToRemove)
    }

    private suspend fun deleteMusic(music: Music) {
        val artists: List<Artist> = getArtistsOfMusicUseCase(musicId = music.musicId).firstOrNull() ?: emptyList()
        val album: Album? = getCorrespondingAlbumUseCase(musicId = music.musicId)

        musicRepository.delete(music = music)

        album?.let { musicAlbum ->
            deleteAlbumIfEmptyUseCase(albumId = musicAlbum.albumId)
        }
        artists.forEach { musicArtist ->
            deleteArtistIfEmptyUseCase(artistId = musicArtist.artistId)
        }
    }

    suspend operator fun invoke(music: Music) {
        deleteMusic(music = music)
    }
}