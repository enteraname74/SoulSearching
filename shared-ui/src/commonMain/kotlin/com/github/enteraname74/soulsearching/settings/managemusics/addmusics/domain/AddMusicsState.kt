package com.github.soulsearching.settings.managemusics.addmusics.domain

import com.github.soulsearching.domain.model.SelectableMusicItem
import com.github.soulsearching.settings.managemusics.addmusics.domain.model.AddMusicsStateType

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
