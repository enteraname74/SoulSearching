package com.github.soulsearching.states

import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.enumsAndTypes.AddMusicsStateType

data class AddMusicsState(
    val state: AddMusicsStateType = AddMusicsStateType.FETCHING_MUSICS,
    val fetchedMusics: ArrayList<SelectableMusicItem> = ArrayList()
)
