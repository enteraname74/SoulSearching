package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudArtist
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.repository.ArtistRepository

class CloudArtistToArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        cloudArtist: CloudArtist,
        mergeMode: MergeMode,
        scope: Scope,
        cachedArtists: Set<Artist>,
    ): Artist {
        val existingArtist: Artist? = getExistingArtist(
            cloudArtist = cloudArtist,
            cachedArtists = cachedArtists,
        )

        val builtArtist: Artist = existingArtist?.merge(
            cloudArtist = cloudArtist,
            mergeMode = mergeMode,
            scope = scope,
        ) ?: cloudArtist.toNewArtist(scope = scope)

        return builtArtist
    }

    private suspend fun getExistingArtist(
        cloudArtist: CloudArtist,
        cachedArtists: Set<Artist>,
    ): Artist? =
        cachedArtists.find { it.remoteId == cloudArtist.id } ?:
        cachedArtists.find { it.artistName == cloudArtist.name} ?:
        artistRepository.getFromRemoteId(cloudArtist.id) ?:
        artistRepository.getFromName(cloudArtist.name)
}
