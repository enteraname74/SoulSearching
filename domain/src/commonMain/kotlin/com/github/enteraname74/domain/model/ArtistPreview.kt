package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

data class ArtistPreview(
    val id: Uuid,
    val name: String,
    val totalMusics: Int,
    val cover: Cover?,
    val nbPlayed: Int,
    override val isInQuickAccess: Boolean
) : QuickAccessible
