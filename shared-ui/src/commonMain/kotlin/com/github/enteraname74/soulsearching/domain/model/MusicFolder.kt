package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Represent a list of musics from a folder.
 */
data class MusicFolder(
    val path: String = "",
    val musics: List<Music> = emptyList(),
    val coverId: UUID? = null
)
