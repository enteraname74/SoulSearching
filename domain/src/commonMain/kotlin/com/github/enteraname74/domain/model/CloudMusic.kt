package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class CloudMusic(
    val fingerprint: String,
    val name: String,
    val album: CloudAlbum,
    val artists: List<CloudArtist>,
    val path: String,
    val albumPosition: Int?,
    val coverPath: String,
    val duration: Long,
    val addedDateMillis: Long,
    val lastUpdateAtMillis: Long,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val scope: Scope,
) {
    fun toNewMusic(
        album: Album,
        artists: List<Artist>,
    ): Music =
        Music(
            musicId = UUID.randomUUID(),
            remoteId = fingerprint,
            name = name,
            album = album,
            artists = artists,
            cover = Cover.Url(coverPath),
            albumPosition = albumPosition,
            localPath = null,
            remotePath = path,
            // TODO CLOUD: Better cloud folder indication?
            folder = "Cloud",
            duration = duration,
            addedDate = LocalDateTime.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            isHidden = false,
            lastUpdatedMillis = lastUpdateAtMillis,
            scope = scope,
        )
}