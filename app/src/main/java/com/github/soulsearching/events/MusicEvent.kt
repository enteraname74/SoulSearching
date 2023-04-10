package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Music

sealed interface MusicEvent {
    object SaveMusic: MusicEvent
    data class SetName(val name : String): MusicEvent
    data class SetCover(val cover : Bitmap): MusicEvent
    data class DeleteMusic(val music : Music): MusicEvent
}