package com.github.soulsearching.events

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.types.AddMusicsStateType

/**
 * Events related to the operation of adding musics.
 */
sealed interface AddMusicsEvent {
    data object ResetState: AddMusicsEvent
    data class SetState(val newState: AddMusicsStateType): AddMusicsEvent
    data class AddFetchedMusics(val musics: ArrayList<SelectableMusicItem>): AddMusicsEvent
    data class SetSelectedMusic(val music: Music, val isSelected: Boolean): AddMusicsEvent
}