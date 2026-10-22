package com.github.enteraname74.soulsearching.remote.model.upload

import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.CoverMode
import com.github.enteraname74.soulsearching.domain.model.toCoverMode
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CollectionUpload(
    val id: Uuid?,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artistIds: List<Uuid>,
    val albumIds: List<Uuid>,
    val coverMode: CoverMode? = null,
)

fun CollectionSyncData.toCollectionUpload(): CollectionUpload = CollectionUpload(
    id = collection.remoteId,
    name = collection.name,
    nbPlayed = collection.nbPlayed,
    isInQuickAccess = collection.isInQuickAccess,
    artistIds = artistRemoteIds,
    albumIds = albumRemoteIds,
    coverMode = collection.cover.toCoverMode(),
)
