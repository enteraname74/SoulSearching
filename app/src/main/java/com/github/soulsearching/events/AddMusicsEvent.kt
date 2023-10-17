package com.github.soulsearching.events

import com.github.soulsearching.database.model.MusicWithCover

interface AddMusicsEvent {
    object ResetState: AddMusicsEvent
    data class SetFetchingState(val isFetching: Boolean): AddMusicsEvent
    data class AddFetchedMusics(val musics: ArrayList<MusicWithCover>): AddMusicsEvent
}