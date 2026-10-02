package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.model.Album
import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.model.CloudMusic
import com.github.enteraname74.soulsearching.domain.model.MergeMode
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.usecase.album.CloudAlbumToAlbumUseCase
import com.github.enteraname74.soulsearching.domain.usecase.artist.CloudArtistToArtistUseCase
import kotlin.uuid.Uuid

class CloudMusicToMusicUseCase(
    private val cloudAlbumToAlbumUseCase: CloudAlbumToAlbumUseCase,
    private val cloudArtistToArtistUseCase: CloudArtistToArtistUseCase,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        cloudMusic: CloudMusic,
        mergeMode: MergeMode,
        cachedArtists: Set<Artist>,
        cachedAlbums: Set<Album>,
        cachedMusics: Set<Music>,
    ): Music {
        val artistsOfMusic: List<Artist> = cloudMusic.artists.map {
            cloudArtistToArtistUseCase(
                cloudArtist = it,
                mergeMode = mergeMode,
                scope = cloudMusic.scope,
                cachedArtists = cachedArtists,
            )
        }
        val albumOfMusic: Album = cloudAlbumToAlbumUseCase(
            cloudAlbum = cloudMusic.album,
            mergeMode = mergeMode,
            scope = cloudMusic.scope,
            cachedArtists = cachedArtists + artistsOfMusic,
            cachedAlbums = cachedAlbums,
        )

        val existingMusic: Music? = getExistingMusic(
            cloudMusic = cloudMusic,
            albumId = albumOfMusic.albumId,
            cachedMusics = cachedMusics,
        )

        val builtMusic: Music = existingMusic?.merge(
            cloudMusic = cloudMusic,
            album = albumOfMusic,
            artists = artistsOfMusic,
            mergeMode = mergeMode,
        ) ?: cloudMusic.toNewMusic(
            album = albumOfMusic,
            artists = artistsOfMusic,
        )
        return builtMusic
    }

    private suspend fun getExistingMusic(
        cloudMusic: CloudMusic,
        albumId: Uuid,
        cachedMusics: Set<Music>,
    ): Music? =
        cachedMusics.find { it.remoteId == cloudMusic.id } ?:
        cachedMusics.find { it.name == cloudMusic.name && cloudMusic.album.id == albumId } ?:
        musicRepository.getFromRemoteId(cloudMusic.id)
            ?: musicRepository.getFromInformation(
                musicName = cloudMusic.name,
                albumId = albumId,
            )
}
