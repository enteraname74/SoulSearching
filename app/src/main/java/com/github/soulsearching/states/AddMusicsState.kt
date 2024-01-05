package com.github.soulsearching.states

import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.enumsAndTypes.AddMusicsStateType

/**
 * State for managing adding musics.
 */
data class AddMusicsState(
    /**
     * Current state of the fetching operation.
     */
    val state: AddMusicsStateType = AddMusicsStateType.FETCHING_MUSICS,
    val fetchedMusics: ArrayList<SelectableMusicItem> = ArrayList()
)
