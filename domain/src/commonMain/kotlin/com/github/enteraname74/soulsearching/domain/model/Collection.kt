package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.math.max

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
                cover = buildNewCover(cloudCollectionInfo.coverPath),
            )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudCollectionInfo.id,
                name = cloudCollectionInfo.name,
                cover = buildNewCover(cloudCollectionInfo.coverPath),
                nbPlayed = max(nbPlayed, cloudCollectionInfo.nbPlayed),
                isInQuickAccess = cloudCollectionInfo.isInQuickAccess,
                lastUpdatedMillis = cloudCollectionInfo.lastUpdateAtMillis,
            )
        }

    private fun buildNewCover(cloudCoverPath: String?): Cover? = when {
        cover is Cover.Url -> cloudCoverPath?.let { Cover.Url(it, null) }
        cover?.isEmpty() == false -> cover
        else -> cloudCoverPath?.let { Cover.Url(it, null) }
    }
}
