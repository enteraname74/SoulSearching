package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover

data class RoomCompleteAlbum(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumns = ["artistId"],
        entityColumns = ["artistId"],
    )
    val roomArtist: RoomArtist
) {
    fun toAlbum(): Album {
        val localCover = Cover.CoverFile(fileCoverId = roomAlbum.coverId)
        val remoteCover = roomAlbum.coverUrl?.let { Cover.Url(it) }

        val usedCover = if (remoteCover == null) {
            localCover
        } else {
            localCover.takeIf { !it.isEmpty() } ?: remoteCover
        }

        return Album(
            albumId = roomAlbum.albumId,
            albumName = roomAlbum.albumName,
            artist = roomArtist.toArtist(),
            cover = usedCover,
            addedDate = roomAlbum.addedDate,
            nbPlayed = roomAlbum.nbPlayed,
            isInQuickAccess = roomAlbum.isInQuickAccess,
            remoteId = roomAlbum.remoteId,
            lastUpdateMillis = roomAlbum.lastUpdatedMillis,
            scope = roomAlbum.scope,
        )
    }
}
