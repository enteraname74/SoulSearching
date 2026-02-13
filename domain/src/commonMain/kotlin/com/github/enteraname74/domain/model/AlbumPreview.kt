package com.github.enteraname74.domain.model

import java.util.UUID

data class AlbumPreview(
    val id: UUID,
    val nbPlayed: Int,
    val name: String,
    val artist: String,
    val cover: Cover?,
    override val isInQuickAccess: Boolean,
) : QuickAccessible
