package com.github.soulsearching.events

import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.enumsAndTypes.AddMusicsStateType
import com.github.soulsearching.database.model.Music

interface AddMusicsEvent {
    object ResetState: AddMusicsEvent
    data class SetState(val newState: AddMusicsStateType): AddMusicsEvent
    data class AddFetchedMusics(val musics: ArrayList<SelectableMusicItem>): AddMusicsEvent
    data class SetSelectedMusic(val music: Music, val isSelected: Boolean): AddMusicsEvent
}