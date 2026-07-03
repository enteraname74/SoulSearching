package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover

data class RoomCompleteAlbum(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId",
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
