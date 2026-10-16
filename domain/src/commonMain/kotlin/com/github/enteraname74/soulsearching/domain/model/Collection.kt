package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Collection(
    val collectionId: Uuid = Uuid.random(),
    val remoteId: Uuid? = null,
    val name: String,
    val cover: Cover? = null,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long? = DateUtils.now(),
) {
    fun merge(cloudCollectionInfo: CloudCollection.Info, mergeMode: MergeMode): Collection =
        when (mergeMode) {
            MergeMode.LocalFirst -> copy(
                remoteId = cloudCollectionInfo.id,
                cover = buildNewCover(remoteCover = cloudCollectionInfo.cover),
            )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudCollectionInfo.id,
                name = cloudCollectionInfo.name,
                cover = buildNewCover(remoteCover = cloudCollectionInfo.cover),
                nbPlayed = max(nbPlayed, cloudCollectionInfo.nbPlayed),
                isInQuickAccess = cloudCollectionInfo.isInQuickAccess,
                lastUpdatedMillis = cloudCollectionInfo.lastUpdateAtMillis,
            )
        }

    private fun buildNewCover(
        remoteCover: RemoteCover?,
    ): Cover? =
        when {
            // If the saved cover is already an Url, we will always overwrite it with latest cloud info
            (cover is Cover.Url) || ((cover as? Cover.Grid)?.list?.all { it is Cover.Url } == true) -> remoteCover?.toCover()
            cover?.isEmpty() == false -> cover
            else -> remoteCover?.toCover()
        }
}
