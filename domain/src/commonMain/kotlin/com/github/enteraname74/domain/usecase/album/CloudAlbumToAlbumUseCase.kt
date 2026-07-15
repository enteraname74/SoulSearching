package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudAlbum
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.usecase.artist.CloudArtistToArtistUseCase

class CloudAlbumToAlbumUseCase(
    private val cloudArtistToArtistUseCase: CloudArtistToArtistUseCase,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        cloudAlbum: CloudAlbum,
        mergeMode: MergeMode,
        scope: Scope,
        cachedArtists: Set<Artist>,
        cachedAlbums: Set<Album>,
    ): Album {
        val existingAlbum: Album? = getExistingAlbum(
            cloudAlbum = cloudAlbum,
            cachedAlbums = cachedAlbums,
        )
        val albumArtist: Artist = cloudArtistToArtistUseCase(
            cloudArtist = cloudAlbum.artist,
            mergeMode = mergeMode,
            scope = scope,
            cachedArtists = cachedArtists,
        )

        val builtAlbum: Album = existingAlbum?.merge(
            cloudAlbum = cloudAlbum,
            artist = albumArtist,
            mergeMode = mergeMode,
            scope = scope,
        )
            ?: cloudAlbum.toNewAlbum(
                artist = albumArtist,
                scope = scope,
            )

        return builtAlbum
    }

    private suspend fun getExistingAlbum(
        cloudAlbum: CloudAlbum,
        cachedAlbums: Set<Album>,
    ): Album? =
        cachedAlbums.find { it.remoteId == cloudAlbum.id } ?:
        cachedAlbums.find { it.albumName == cloudAlbum.name && it.artist.artistName == cloudAlbum.artist.name } ?:
        albumRepository.getFromRemoteId(cloudAlbum.id)
            ?: albumRepository.getFromInformation(
                albumName = cloudAlbum.name,
                artistName = cloudAlbum.artist.name,
            )
}
