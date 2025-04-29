package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun PlaylistBottomSheetMenu(
    selectedPlaylist: PlaylistWithMusicsNumber,
    modifyAction : () -> Unit,
    playNextAction: () -> Unit,
    addToQueueAction: () -> Unit,
    deleteAction : () -> Unit,
    quickAccessAction : () -> Unit,
    isInQuickAccess: Boolean,
    isPlayedListEmpty: Boolean,
    removeFromPlayedListAction: () -> Unit,
) {

    Column {
        BottomSheetElementInformation(
            title = selectedPlaylist.playlist.name,
            subTitle = strings.musics(total = selectedPlaylist.musicsNumber),
            cover = selectedPlaylist.cover,
        )
        QuickAccessBottomSheetMenu(
            isElementInQuickAccess = isInQuickAccess,
            quickAccessAction = quickAccessAction,
        ) {
            BottomSheetRow(
                icon = Icons.Rounded.Edit,
                text = strings.modifyPlaylist,
                onClick = modifyAction
            )
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
            )
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                text = strings.addToQueue,
                onClick = addToQueueAction,
            )
            if (!isPlayedListEmpty) {
                BottomSheetRow(
                    icon = Icons.Rounded.PlaylistRemove,
                    text = strings.removeFromPlayedList,
                    onClick = removeFromPlayedListAction,
                )
            }
            if (!selectedPlaylist.playlist.isFavorite) {
                BottomSheetRow(
                    icon = Icons.Rounded.Delete,
                    text = strings.deletePlaylist,
                    onClick = deleteAction
                )
            }
        }
    }
}