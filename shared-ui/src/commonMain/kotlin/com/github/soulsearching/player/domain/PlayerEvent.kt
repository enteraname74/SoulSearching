package com.github.soulsearching.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import java.util.UUID

/**
 * Events for the player view.
 */
sealed interface PlayerEvent {
    data class SetDeleteMusicDialogVisibility(val isShown: Boolean): PlayerEvent

    data class SetMusicBottomSheetVisibility(val isShown: Boolean): PlayerEvent

    data class SetAddToPlaylistBottomSheetVisibility(val isShown: Boolean): PlayerEvent

    data class DeleteMusic(val musicId: UUID): PlayerEvent

    data class ToggleQuickAccessState(val musicId: UUID): PlayerEvent

    data class AddMusicToPlaylists(val musicId: UUID, val selectedPlaylistsIds: List<UUID>): PlayerEvent

    data object ToggleFavoriteState: PlayerEvent
    data class SetPlayedList(val playedList: List<Music>): PlayerEvent
    data class SetPlayerMode(val playerMode: PlayerMode): PlayerEvent
    data class SetCurrentMusic(val currentMusic: Music?): PlayerEvent
    data class SetCurrentMusicPosition(val position: Int): PlayerEvent
    data class SetIsPlaying(val isPlaying: Boolean): PlayerEvent

    data class SetCurrentMusicCover(val cover: ImageBitmap?): PlayerEvent

    data object GetLyrics: PlayerEvent
}