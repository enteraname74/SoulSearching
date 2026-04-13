package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudArtist
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.repository.ArtistRepository

class UpsertCloudArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        cloudArtist: CloudArtist,
        mergeMode: MergeMode,
        scope: Scope,
    ): Artist {
        val existingArtist: Artist? = getExistingArtist(cloudArtist)

        val savedArtist: Artist = existingArtist?.merge(
            cloudArtist = cloudArtist,
            mergeMode = mergeMode,
            scope = scope,
        ) ?: cloudArtist.toNewArtist()

        artistRepository.upsert(savedArtist)

        return savedArtist
    }

    private suspend fun getExistingArtist(
        cloudArtist: CloudArtist,
    ): Artist? =
        artistRepository.getFromRemoteId(cloudArtist.id) ?:
        artistRepository.getFromName(cloudArtist.name)
}