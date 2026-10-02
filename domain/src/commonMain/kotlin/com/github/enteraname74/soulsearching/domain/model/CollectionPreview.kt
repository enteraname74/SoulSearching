package com.github.enteraname74.soulsearching.domain.model

import kotlin.uuid.Uuid

data class CollectionPreview(
    val id: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val totalMusics: Int,
    val nbPlayed: Int,
)
