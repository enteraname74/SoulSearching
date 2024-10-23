package com.github.enteraname74.soulsearching.features.filemanager.musicfetching

import com.github.enteraname74.domain.model.Music

/**
 * Define information about a song, its cover and if it is selected.
 */
data class SelectableMusicItem(
    val music: Music,
    var isSelected: Boolean
)
