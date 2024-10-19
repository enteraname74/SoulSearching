package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState

@Composable
fun MusicBottomSheetMenu(
    modifyAction: () -> Unit,
    removeAction: () -> Unit,
    removeFromPlaylistAction: () -> Unit,
    removeFromPlayedListAction: () -> Unit,
    quickAccessAction: () -> Unit,
    addToPlaylistAction: () -> Unit,
    playNextAction : () -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isInQuickAccess: Boolean,
    isCurrentlyPlaying: Boolean,
    isPlayedListEmpty: Boolean,
) {
    QuickAccessBottomSheetMenu(
        isElementInQuickAccess = isInQuickAccess,
        quickAccessAction = quickAccessAction,
    ) {
        BottomSheetRow(
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            text = strings.addToPlaylist,
            onClick = addToPlaylistAction,
        )
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = strings.modifyMusic,
            onClick = modifyAction,
        )
        if (!isCurrentlyPlaying) {
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYLIST) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.removeFromPlaylist,
                onClick = removeFromPlaylistAction,
            )
        }
        if (!isPlayedListEmpty) {
            BottomSheetRow(
                icon = Icons.Rounded.PlaylistRemove,
                text = strings.removeFromPlayedList,
                onClick = removeFromPlayedListAction,
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = strings.deleteMusic,
            onClick = removeAction,
        )
    }
}