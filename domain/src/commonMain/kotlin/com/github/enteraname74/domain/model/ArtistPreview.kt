package com.github.enteraname74.domain.model

import java.util.UUID

data class ArtistPreview(
    val id: UUID,
    val name: String,
    val totalMusics: Int,
    val cover: Cover?,
    override val isInQuickAccess: Boolean
) : QuickAccessible