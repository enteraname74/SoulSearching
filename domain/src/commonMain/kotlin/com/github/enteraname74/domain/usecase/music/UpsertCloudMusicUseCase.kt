package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.UpsertCloudAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertCloudArtistUseCase
import com.github.enteraname74.domain.usecase.musicartist.CommonMusicArtistUseCase
import java.util.UUID

class UpsertCloudMusicUseCase(
    private val upsertCloudAlbumUseCase: UpsertCloudAlbumUseCase,
    private val upsertCloudArtistUseCase: UpsertCloudArtistUseCase,
    private val musicRepository: MusicRepository,
    private val commonMusicArtistUseCase: CommonMusicArtistUseCase,
) {
    suspend operator fun invoke(cloudMusic: CloudMusic): Music {
        val artistsOfMusic: List<Artist> = cloudMusic.artists.map {
            upsertCloudArtistUseCase(it)
        }
        val albumOfMusic: Album = upsertCloudAlbumUseCase(cloudMusic.album)

        val existingMusic: Music? = getExistingMusic(
            cloudMusic = cloudMusic,
            albumId = albumOfMusic.albumId,
        )

        val savedMusic: Music = existingMusic?.merge(
            cloudMusic = cloudMusic,
            album = albumOfMusic,
            artists = artistsOfMusic,
        ) ?: cloudMusic.toNewMusic(
            album = albumOfMusic,
            artists = artistsOfMusic,
        )

        musicRepository.upsert(savedMusic)
        commonMusicArtistUseCase.setArtistsOfMusic(
            musicId = savedMusic.musicId,
            artistIds = artistsOfMusic.map { it.artistId },
        )

        return savedMusic
    }

    private suspend fun getExistingMusic(
        cloudMusic: CloudMusic,
        albumId: UUID,
    ): Music? =
        musicRepository.getFromRemoteId(cloudMusic.fingerprint)
            ?: musicRepository.getFromInformation(
                musicName = cloudMusic.name,
                albumId = albumId,
            )
}