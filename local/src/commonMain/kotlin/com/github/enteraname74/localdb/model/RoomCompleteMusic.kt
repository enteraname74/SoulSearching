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
    fun toMusic(): Music {
        val localCover = Cover.CoverFile(
            initialCoverPath = music.localPath,
            fileCoverId = music.coverId,
        )
        val remoteCover = music.coverUrl?.let { Cover.Url(it) }

        val usedCover = if (remoteCover == null) {
            localCover
        } else {
            localCover.takeIf { !it.isEmpty() } ?: remoteCover
        }

        return Music(
            musicId = music.musicId,
            name = music.name,
            album = completeAlbum.toAlbum(),
            artists = artists.map { it.toArtist() },
            cover = usedCover,
            albumPosition = music.albumPosition,
            localPath = music.localPath,
            folder = music.folder,
            duration = music.duration,
            addedDate = music.addedDate,
            nbPlayed = music.nbPlayed,
            isInQuickAccess = music.isInQuickAccess,
            isHidden = music.isHidden,
            remoteId = music.remoteId,
            remotePath = music.remotePath,
            lastUpdatedMillis = music.lastUpdateMillis,
            scope = music.scope,
        )
    }
}
