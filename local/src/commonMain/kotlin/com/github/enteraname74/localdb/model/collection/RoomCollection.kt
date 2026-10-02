package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.PrimaryKey
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
