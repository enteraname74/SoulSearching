package com.github.enteraname74.soulsearching.domain.model

import kotlin.uuid.Uuid

data class ArtistPreview(
    val id: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val totalMusics: Int,
    val cover: Cover.Simple?,
    val nbPlayed: Int,
    override val isInQuickAccess: Boolean
) : QuickAccessible
