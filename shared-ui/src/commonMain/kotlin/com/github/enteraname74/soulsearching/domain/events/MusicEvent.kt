package com.github.enteraname74.soulsearching.domain.events

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events related to musics.
 */
sealed interface MusicEvent {
    data class SetSortDirection(val direction: Int) : MusicEvent
    data class SetSortType(val type: Int) : MusicEvent
}