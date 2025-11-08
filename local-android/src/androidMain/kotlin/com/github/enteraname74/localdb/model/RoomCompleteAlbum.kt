package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Cover

internal data class RoomCompleteAlbum(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId",
    )
    val roomArtist: RoomArtist
) {
    fun toAlbum(): Album =
        Album(
            albumId = roomAlbum.albumId,
            albumName = roomAlbum.albumName,
            artist = roomArtist.toArtist(),
            cover = Cover.CoverFile(fileCoverId = roomAlbum.coverId),
            addedDate = roomAlbum.addedDate,
            nbPlayed = roomAlbum.nbPlayed,
            isInQuickAccess = roomAlbum.isInQuickAccess,
        )
}