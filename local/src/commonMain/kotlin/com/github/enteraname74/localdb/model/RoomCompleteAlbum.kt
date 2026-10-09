package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.soulsearching.domain.model.Album

data class RoomCompleteAlbum(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumns = ["artistId"],
        entityColumns = ["artistId"],
    )
    val roomArtist: RoomArtist
) {
    fun toAlbum(): Album =
        Album(
            albumId = roomAlbum.albumId,
            albumName = roomAlbum.albumName,
            artist = roomArtist.toArtist(),
            cover = roomAlbum.cover?.toSimpleCover(null),
            addedDate = roomAlbum.addedDate,
            nbPlayed = roomAlbum.nbPlayed,
            isInQuickAccess = roomAlbum.isInQuickAccess,
            remoteId = roomAlbum.remoteId,
            lastUpdateMillis = roomAlbum.lastUpdatedMillis,
            scope = roomAlbum.scope,
        )
}
