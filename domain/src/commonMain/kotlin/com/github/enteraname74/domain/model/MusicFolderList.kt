package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Represent a list of musics from a folder.
 */
data class MusicFolderList(
    val path: String = "",
    val musics: List<Music> = emptyList(),
    val coverId: UUID? = null
)