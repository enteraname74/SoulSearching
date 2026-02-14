package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Represent a playlist with the total of songs it possess.
 */
data class PlaylistPreview(
    val id: UUID,
    val isFavorite: Boolean,
    val name: String,
    val totalMusics : Int,
    val cover: Cover?,
    override val isInQuickAccess: Boolean,
): QuickAccessible
