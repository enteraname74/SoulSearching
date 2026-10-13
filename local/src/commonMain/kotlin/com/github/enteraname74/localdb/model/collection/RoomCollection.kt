package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.Cover
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity
data class RoomCollection(
    @PrimaryKey
    val collectionId: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val coverId: Uuid?,
    val coverUrl: String?,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val lastUpdatedMillis: Long?,
)

internal fun RoomCollection.toCollection(): Collection {
    val localCover = Cover.CoverFile(fileCoverId = coverId)
    val remoteCover = coverUrl?.let { Cover.Url(it, localCover) }

    return Collection(
        collectionId = collectionId,
        remoteId = remoteId,
        name = name,
        cover = localCover.takeIf { !it.isEmpty() } ?: remoteCover,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        lastUpdatedMillis = lastUpdatedMillis,
    )
}

internal fun Collection.toRoomCollection(): RoomCollection = RoomCollection(
    collectionId = collectionId,
    remoteId = remoteId,
    name = name,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    coverUrl = (cover as? Cover.Url)?.url,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    lastUpdatedMillis = lastUpdatedMillis,
)
