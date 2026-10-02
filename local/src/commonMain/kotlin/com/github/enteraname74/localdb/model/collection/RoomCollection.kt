package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.soulsearching.domain.model.Collection
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity
data class RoomCollection(
    @PrimaryKey
    val collectionId: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val lastUpdatedMillis: Long?,
)

internal fun RoomCollection.toCollection(): Collection = Collection(
    collectionId = collectionId,
    remoteId = remoteId,
    name = name,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    lastUpdatedMillis = lastUpdatedMillis,
)

internal fun Collection.toRoomCollection(): RoomCollection = RoomCollection(
    collectionId = collectionId,
    remoteId = remoteId,
    name = name,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    lastUpdatedMillis = lastUpdatedMillis,
)
