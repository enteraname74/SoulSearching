package com.github.enteraname74.soulsearching.domain.model

import kotlin.uuid.Uuid

data class CollectionSyncData(
    val collection: Collection,
    val artistRemoteIds: List<Uuid>,
    val albumRemoteIds: List<Uuid>,
)
