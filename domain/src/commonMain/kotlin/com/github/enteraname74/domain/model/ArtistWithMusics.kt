package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs

/**
 * Represent an artist with its musics.
 */
data class ArtistWithMusics(
    val artist: Artist,
    val musics: List<Music>,
    override val isInQuickAccess: Boolean = artist.isInQuickAccess,
) : QuickAccessible {
    val cover: Cover? = if (artist.cover?.isEmpty() == false) {
        artist.cover.ifCoverFile { coverFile ->
            coverFile.copy(
                devicePathSpec = coverFile.devicePathSpec?.copy(
                    fallback = if (coverFile.fileCoverId == null) {
                        musics.coverFromSongs()
                    } else {
                        coverFile.devicePathSpec.fallback
                    }
                )
            )
        }
    } else {
        musics.coverFromSongs()
    }
}