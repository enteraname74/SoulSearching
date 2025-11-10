package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music

data class RoomCompleteMusic(
    @Embedded val music: RoomMusic,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
        entity = RoomAlbum::class,
    )
    val completeAlbum: RoomCompleteAlbum,
    @Relation(
        parentColumn = "musicId",
        entityColumn = "artistId",
        associateBy = Junction(RoomMusicArtist::class),
    )
    val artists: List<RoomArtist>
) {
    fun toMusic(): Music =
        Music(
            musicId = music.musicId,
            name = music.name,
            album = completeAlbum.toAlbum(),
            artists = artists.map { it.toArtist() },
            cover = Cover.CoverFile(
                initialCoverPath = music.path,
                fileCoverId = music.coverId,
            ),
            albumPosition = music.albumPosition,
            path = music.path,
            folder = music.folder,
            duration = music.duration,
            addedDate = music.addedDate,
            nbPlayed = music.nbPlayed,
            isInQuickAccess = music.isInQuickAccess,
            isHidden = music.isHidden,
        )
}
