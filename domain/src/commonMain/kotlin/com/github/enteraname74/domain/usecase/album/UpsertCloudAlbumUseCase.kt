package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudAlbum
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.usecase.artist.UpsertCloudArtistUseCase

class UpsertCloudAlbumUseCase(
    private val upsertCloudArtistUseCase: UpsertCloudArtistUseCase,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        cloudAlbum: CloudAlbum,
        mergeMode: MergeMode,
        scope: Scope,
    ): Album {
        val existingAlbum: Album? = getExistingAlbum(cloudAlbum)
        val albumArtist: Artist = upsertCloudArtistUseCase(
            cloudArtist = cloudAlbum.artist,
            mergeMode = mergeMode,
            scope = scope,
        )

        val savedAlbum: Album = existingAlbum?.merge(
            cloudAlbum = cloudAlbum,
            artist = albumArtist,
            mergeMode = mergeMode,
            scope = scope,
        )
            ?: cloudAlbum.toNewAlbum(
                artist = albumArtist,
                scope = scope,
            )

        albumRepository.upsert(savedAlbum)

        return savedAlbum
    }

    private suspend fun getExistingAlbum(cloudAlbum: CloudAlbum): Album? =
        albumRepository.getFromRemoteId(cloudAlbum.id) ?: albumRepository.getFromInformation(
            albumName = cloudAlbum.name,
            artistName = cloudAlbum.artist.name,
        )
}