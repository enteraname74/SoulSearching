package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class PlaylistWithMusics(
    @Embedded val playlist: Playlist = Playlist(),
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "musicId",
        associateBy = Junction(MusicPlaylist::class)
    )
    val musics : List<Music> = emptyList()
) {
    fun toPlaylistWithMusicsNumber(): PlaylistWithMusicsNumber {
        return PlaylistWithMusicsNumber(
            playlist = playlist,
            musicsNumber = musics.filter { !it.isHidden }.size
        )
    }
}