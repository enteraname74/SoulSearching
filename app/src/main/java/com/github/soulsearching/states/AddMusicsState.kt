package com.github.soulsearching.states

import com.github.soulsearching.database.model.MusicWithCover

data class AddMusicsState(
    val isFetchingMusics: Boolean = true,
    val fetchedMusics: ArrayList<MusicWithCover> = ArrayList()
)
