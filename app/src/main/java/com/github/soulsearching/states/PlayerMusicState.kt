package com.github.soulsearching.states

import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.PlayerWithMusicItem

data class PlayerMusicState(
    val musics: ArrayList<Music> = ArrayList()
)