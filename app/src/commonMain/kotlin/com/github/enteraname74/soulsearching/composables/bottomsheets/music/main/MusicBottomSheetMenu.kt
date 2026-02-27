package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetTopInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MusicBottomSheetMenu(
    topInformation: BottomSheetTopInformation,
    itemsVisibility: MusicBottomSheetItemsVisibility,
    deleteText: String,
    modifyAction: () -> Unit,
    removeAction: () -> Unit,
    removeFromPlaylistAction: () -> Unit,
    removeFromPlayedListAction: () -> Unit,
    quickAccessAction: () -> Unit,
    addToPlaylistAction: () -> Unit,
    playNextAction : () -> Unit,
    addToQueueAction: () -> Unit,
) {
    Column {
        BottomSheetElementInformation(
            title = topInformation.title,
            subTitle = topInformation.subTitle,
            cover = topInformation.cover,
        )

        QuickAccessBottomSheetMenu(
            isElementInQuickAccess = itemsVisibility.isInQuickAccess,
            quickAccessAction = quickAccessAction,
        ) {
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
                text = strings.addToPlaylist,
                onClick = addToPlaylistAction,
            )
            if (itemsVisibility.editEnabled) {
                BottomSheetRow(
                    icon = Icons.Rounded.Edit,
                    text = strings.modifyMusic,
                    onClick = modifyAction,
                )
            }
            if (itemsVisibility.queueActions) {
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
            }
            if (itemsVisibility.inPlaylist) {
                BottomSheetRow(
                    icon = Icons.Rounded.Delete,
                    text = strings.removeFromPlaylist,
                    onClick = removeFromPlaylistAction,
                )
            }
            if (itemsVisibility.removeFromPlayedList) {
                BottomSheetRow(
                    icon = Icons.Rounded.PlaylistRemove,
                    text = strings.removeFromPlayedList,
                    onClick = removeFromPlayedListAction,
                )
            }
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = deleteText,
                onClick = removeAction,
            )
        }
    }
}