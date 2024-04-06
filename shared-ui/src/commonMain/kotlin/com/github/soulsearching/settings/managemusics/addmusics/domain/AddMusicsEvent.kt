package com.github.soulsearching.settings.managemusics.addmusics.domain

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.domain.model.SelectableMusicItem
import com.github.soulsearching.settings.managemusics.addmusics.domain.model.AddMusicsStateType

/**
 * Events related to the operation of adding musics.
 */
sealed interface AddMusicsEvent {
    data object ResetState: AddMusicsEvent
    data class SetState(val newState: AddMusicsStateType): AddMusicsEvent
    data class AddFetchedMusics(val musics: ArrayList<SelectableMusicItem>): AddMusicsEvent
    data class SetSelectedMusic(val music: Music, val isSelected: Boolean): AddMusicsEvent
}