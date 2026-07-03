package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

/**
 * Represent a playlist with the total of songs it possess.
 */
data class PlaylistPreview(
    val id: Uuid,
    val isFavorite: Boolean,
    val name: String,
    val totalMusics : Int,
    val nbPlayed: Int,
    val cover: Cover?,
    override val isInQuickAccess: Boolean,
): QuickAccessible
