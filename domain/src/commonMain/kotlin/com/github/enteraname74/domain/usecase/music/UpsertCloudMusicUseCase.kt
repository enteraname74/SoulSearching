package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.UpsertCloudAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertCloudArtistUseCase
import kotlin.uuid.Uuid

class UpsertCloudMusicUseCase(
    private val upsertCloudAlbumUseCase: UpsertCloudAlbumUseCase,
    private val upsertCloudArtistUseCase: UpsertCloudArtistUseCase,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        cloudMusic: CloudMusic,
        mergeMode: MergeMode,
    ): Music {
        val artistsOfMusic: List<Artist> = cloudMusic.artists.map {
            upsertCloudArtistUseCase(
                cloudArtist = it,
                mergeMode = mergeMode,
                scope = cloudMusic.scope,
            )
        }
        val albumOfMusic: Album = upsertCloudAlbumUseCase(
            cloudAlbum = cloudMusic.album,
            mergeMode = mergeMode,
            scope = cloudMusic.scope,
        )

        val existingMusic: Music? = getExistingMusic(
            cloudMusic = cloudMusic,
            albumId = albumOfMusic.albumId,
        )

        val savedMusic: Music = existingMusic?.merge(
            cloudMusic = cloudMusic,
            album = albumOfMusic,
            artists = artistsOfMusic,
            mergeMode = mergeMode,
        ) ?: cloudMusic.toNewMusic(
            album = albumOfMusic,
            artists = artistsOfMusic,
        )

        musicRepository.upsert(savedMusic)
        return savedMusic
    }

    private suspend fun getExistingMusic(
        cloudMusic: CloudMusic,
        albumId: Uuid,
    ): Music? =
        musicRepository.getFromRemoteId(cloudMusic.fingerprint)
            ?: musicRepository.getFromInformation(
                musicName = cloudMusic.name,
                albumId = albumId,
            )
}
