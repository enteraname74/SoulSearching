package com.github.enteraname74.domain.model

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.math.max

/**
 * Represent a song with information related to it.
 * It does not possess its cover directly.
 */
data class Music(
    val musicId: Uuid = Uuid.random(),
    val remoteId: String?,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val cover: Cover,
    val albumPosition: Int?,
    val localPath: String?,
    val remotePath: String?,
    val folder: String,
    val duration: Long = 0L,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    override val isInQuickAccess: Boolean = false,
    val isHidden: Boolean = false,
    val lastUpdatedMillis: Long?,
    val scope: Scope,
) : QuickAccessible {
    val path: String? = localPath ?: remotePath
    val isRemoteOnly: Boolean = localPath == null && remotePath != null
    val isSynced: Boolean = localPath != null && remotePath != null
    val isLocalOnly: Boolean = localPath != null && remotePath == null

    val informationText: String = "${artists.joinToString { it.artistName }} | ${album.albumName}"

    val artistsNames: String = buildList {
        add(album.artist.artistName)
        addAll(artists.map { it.artistName })
    }.distinct().joinToString { it }

    fun hasPotentialMultipleArtists(): Boolean = artists
        .any {
            it
                .artistName
                .split(",", "&")
                .size > 1
        }

    fun merge(
        cloudMusic: CloudMusic,
        album: Album,
        artists: List<Artist>,
        mergeMode: MergeMode,
    ): Music =
        when (mergeMode) {
            MergeMode.LocalFirst -> copy(
                remoteId = cloudMusic.fingerprint,
                cover = cover.takeIf { !it.isEmpty() } ?: Cover.Url(cloudMusic.coverPath),
            )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudMusic.fingerprint,
                name = cloudMusic.name,
                album = album,
                artists = artists,
                remotePath = cloudMusic.path,
                albumPosition = cloudMusic.albumPosition,
                cover = cover.takeIf { !it.isEmpty() } ?: Cover.Url(cloudMusic.coverPath),
                lastUpdatedMillis = cloudMusic.lastUpdateAtMillis,
                nbPlayed = max(nbPlayed, cloudMusic.nbPlayed),
                isInQuickAccess = cloudMusic.isInQuickAccess,
                scope = cloudMusic.scope,
            )
        }
}
