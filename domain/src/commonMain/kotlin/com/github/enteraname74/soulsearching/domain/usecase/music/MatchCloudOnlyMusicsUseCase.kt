package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.MusicMatchKey
import com.github.enteraname74.soulsearching.domain.model.normalizedForMusicMatching
import com.github.enteraname74.soulsearching.domain.model.toMusicMatchKey
import com.github.enteraname74.soulsearching.domain.repository.MusicRepository
import kotlin.math.abs

/**
 * Reuses the identity of a cloud-only song when a newly discovered local song
 * has one unambiguous metadata match.
 */
class MatchCloudOnlyMusicsUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(localMusics: List<Music>): List<Music> {
        if (localMusics.isEmpty()) return emptyList()

        val localKeys: Set<MusicMatchKey> = localMusics
            .map(Music::toMusicMatchKey)
            .filter(MusicMatchKey::canBeMatched)
            .toSet()

        if (localKeys.isEmpty()) return localMusics

        val cloudOnlyByKey: Map<MusicMatchKey, List<Music>> = musicRepository
            .getCloudOnlyMusicMatchingNames(
                musicNames = localMusics.map { it.name.trim() },
            )
            .asSequence()
            .map { it.toMusicMatchKey() to it }
            .filter { (key, _) -> key in localKeys }
            .groupBy(
                keySelector = { (key, _) -> key },
                valueTransform = { (_, music) -> music },
            )

        return localMusics.map { localMusic ->
            val key = localMusic.toMusicMatchKey()
            val matchingCloudMusic: Music? = cloudOnlyByKey[key]
                .orEmpty()
                .filter { cloudMusic -> cloudMusic.isCompatibleWith(localMusic) }
                .singleOrNull()

            matchingCloudMusic?.withLocalInformationFrom(localMusic) ?: localMusic
        }
    }

    private fun Music.isCompatibleWith(other: Music): Boolean {
        return duration <= 0L ||
            other.duration <= 0L ||
            abs(duration - other.duration) <= DURATION_TOLERANCE_MILLIS
    }

    private fun Music.withLocalInformationFrom(localMusic: Music): Music = copy(
        name = localMusic.name,
        album = album.copy(
            albumName = localMusic.album.albumName,
            artist = album.artist.withNameFrom(localMusic.album.artist),
        ),
        artists = localMusic.artists.map { localArtist ->
            artists.singleOrNull { cloudArtist ->
                cloudArtist.artistName.normalizedForMusicMatching() ==
                    localArtist.artistName.normalizedForMusicMatching()
            }?.withNameFrom(localArtist) ?: localArtist
        },
        cover = localMusic.cover,
        albumPosition = localMusic.albumPosition,
        localPath = localMusic.localPath,
        folder = localMusic.folder,
        duration = localMusic.duration,
    )

    private fun Artist.withNameFrom(localArtist: Artist): Artist = copy(
        artistName = localArtist.artistName,
    )

    private companion object {
        const val DURATION_TOLERANCE_MILLIS = 2_000L
    }
}
