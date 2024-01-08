package com.github.soulsearching.events

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events related to musics.
 */
sealed interface MusicEvent {
    data object UpdateMusic : MusicEvent
    data object DeleteMusic : MusicEvent
    data object UpdateQuickAccessState: MusicEvent
    data class SetSortDirection(val type: Int) : MusicEvent
    data class SetSortType(val type: Int) : MusicEvent
    data class DeleteDialog(val isShown: Boolean) : MusicEvent
    data class RemoveFromPlaylistDialog(val isShown: Boolean) : MusicEvent
    data class BottomSheet(val isShown: Boolean) : MusicEvent
    data class AddToPlaylistBottomSheet(val isShown: Boolean) : MusicEvent
    data class SetSelectedMusic(val music: Music) : MusicEvent
    data class SetName(val name: String) : MusicEvent
    data class SetArtist(val artist: String) : MusicEvent
    data class SetAlbum(val album: String) : MusicEvent
    data class SetCover(val cover: ImageBitmap) : MusicEvent
    data class SetFavorite(val musicId: UUID) : MusicEvent
    data class AddNbPlayed(val musicId: UUID): MusicEvent

}