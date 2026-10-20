package com.github.enteraname74.soulsearching.domain.model

private val extraWhitespace = Regex("\\s+")

/**
 * Metadata used to find a potential local counterpart of a cloud-only song.
 *
 * Duration is deliberately not part of the key: it is used to disambiguate
 * songs after the inexpensive metadata lookup.
 */
data class MusicMatchKey(
    val name: String,
    val albumName: String,
    val artistNames: List<String>,
) {
    val canBeMatched: Boolean =
        name.isNotEmpty() && albumName.isNotEmpty() && artistNames.isNotEmpty()
}

fun Music.toMusicMatchKey(): MusicMatchKey = MusicMatchKey(
    name = name.normalizedForMusicMatching(),
    albumName = album.albumName.normalizedForMusicMatching(),
    artistNames = (artists + album.artist)
        .map { it.artistName.normalizedForMusicMatching() }
        .filter { it.isNotEmpty() }
        .distinct()
        .sorted(),
)

internal fun String.normalizedForMusicMatching(): String =
    trim()
        .lowercase()
        .replace(extraWhitespace, " ")
