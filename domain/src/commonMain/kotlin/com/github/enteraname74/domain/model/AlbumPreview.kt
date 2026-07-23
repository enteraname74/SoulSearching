package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

data class AlbumPreview(
    val id: Uuid,
    val nbPlayed: Int,
    val name: String,
    val artist: String,
    val cover: Cover?,
    override val isInQuickAccess: Boolean,
) : QuickAccessible
