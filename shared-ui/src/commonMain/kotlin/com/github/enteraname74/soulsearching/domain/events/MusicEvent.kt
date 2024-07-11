package com.github.enteraname74.soulsearching.domain.events

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events related to musics.
 */
sealed interface MusicEvent {
    data class DeleteMusic(val musicId: UUID) : MusicEvent
    data class ToggleQuickAccessState(val music: Music): MusicEvent
    data class SetSortDirection(val direction: Int) : MusicEvent
    data class SetSortType(val type: Int) : MusicEvent
    data class DeleteDialog(val isShown: Boolean) : MusicEvent
    data class RemoveFromPlaylistDialog(val isShown: Boolean) : MusicEvent
    data class BottomSheet(val isShown: Boolean) : MusicEvent
    data class AddToPlaylistBottomSheet(val isShown: Boolean) : MusicEvent
    data class SetSelectedMusic(val music: Music) : MusicEvent
    data class SetFavorite(val musicId: UUID) : MusicEvent
}