package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.MusicPlaylist

sealed interface MusicEvent {
    object AddMusic : MusicEvent
    object UpdateMusic : MusicEvent
    object DeleteMusic : MusicEvent
    data class DeleteDialog(val isShown : Boolean) : MusicEvent
    data class SetSelectedMusic(val music : Music) : MusicEvent
    data class SetName(val name: String) : MusicEvent
    data class SetArtist(val artist: String) : MusicEvent
    data class SetAlbum(val album: String) : MusicEvent
    data class SetCover(val cover: Bitmap) : MusicEvent
    data class DeleteMusicFromPlaylist(val musicPlaylist: MusicPlaylist) : MusicEvent
}