package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.DateUtils
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Represent a playlist with information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Playlist(
    val playlistId: Uuid = Uuid.random(),
    val remoteId: Uuid? = null,
    val name: String = "",
    val cover: Cover? = null,
    val isFavorite: Boolean = false,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long? = DateUtils.now(),
) {
    fun merge(
        cloudPlaylistInfo: CloudPlaylist.Info,
        mergeMode: MergeMode,
    ): Playlist {
        return when (mergeMode) {
            MergeMode.LocalFirst -> copy(
                remoteId = cloudPlaylistInfo.id,
                cover = buildNewCover(cloudCoverPath = cloudPlaylistInfo.coverPath),
            )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudPlaylistInfo.id,
                name = cloudPlaylistInfo.name,
                cover = buildNewCover(cloudCoverPath = cloudPlaylistInfo.coverPath),
                isFavorite = cloudPlaylistInfo.isFavorite,
                nbPlayed = max(nbPlayed, cloudPlaylistInfo.nbPlayed),
                isInQuickAccess = cloudPlaylistInfo.isInQuickAccess,
                lastUpdatedMillis = cloudPlaylistInfo.lastUpdateAtMillis,
            )
        }
    }

    private fun buildNewCover(
        cloudCoverPath: String?
    ): Cover? =
        when {
            // If the saved cover is already an Url, we will always overwrite it with latest cloud info
            cover is Cover.Url -> cloudCoverPath?.let { Cover.Url(it, null) }
            cover?.isEmpty() == false -> cover
            else -> cloudCoverPath?.let { Cover.Url(it, null) }
        }
}
