package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music

data class RoomCompleteMusic(
    @Embedded val music: RoomMusic,
    @Relation(
        parentColumns = ["albumId"],
        entityColumns = ["albumId"],
        entity = RoomAlbum::class,
    )
    val completeAlbum: RoomCompleteAlbum,
    @Relation(
        parentColumns = ["musicId"],
        entityColumns = ["artistId"],
        associateBy = Junction(RoomMusicArtist::class),
    )
    val artists: List<RoomArtist>
) {
    fun toMusic(): Music {
        val localCover = Cover.CoverFile(
            initialCoverPath = music.localPath,
            fileCoverId = music.coverId,
        )
        val remoteCover = music.coverUrl?.let { Cover.Url(it, localCover) }

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
