package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Music

data class MusicState(
    val musics: List<Music> = emptyList(),
    val name : String = "",
    val cover : Bitmap? = null
)