package com.github.enteraname74.soulsearching.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class CloudCollection(
    val collection: Info,
    val artistIds: List<Uuid>,
    val albumIds: List<Uuid>,
) {
    @Serializable
    data class Info(
        val id: Uuid,
        val userId: Uuid,
        val name: String,
        val cover: RemoteCover?,
        val addedDateMillis: Long,
        val nbPlayed: Int,
        val isInQuickAccess: Boolean,
        val lastUpdateAtMillis: Long,
    )

    fun toNewCollection(): Collection = with(collection) {
        Collection(
            collectionId = Uuid.random(),
            remoteId = id,
            name = name,
            cover = cover?.toCover(),
            addedDate = Instant.fromEpochMilliseconds(addedDateMillis),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdatedMillis = lastUpdateAtMillis,
        )
    }
}
