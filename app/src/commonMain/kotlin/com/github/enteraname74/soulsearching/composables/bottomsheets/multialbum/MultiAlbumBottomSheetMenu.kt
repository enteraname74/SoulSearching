package com.github.enteraname74.soulsearching.composables.bottomsheets.multialbum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowUi
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MultiAlbumBottomSheetMenu(
    total: Int,
    deleteAction: () -> Unit,
    playNextAction: () -> Unit,
    addToQueueAction: () -> Unit,
    isPlayedListEmpty: Boolean,
    removeFromPlayedListAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
    ) {
        BottomSheetElementInformation(
            title = strings.multipleSelection,
            subTitle = strings.selectedElements(total = total),
            cover = null,
        )
        BottomSheetRowUi(
            icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
            text = strings.playNext,
            onClick = playNextAction,
        )
        BottomSheetRowUi(
            icon = Icons.AutoMirrored.Rounded.QueueMusic,
            text = strings.addToQueue,
            onClick = addToQueueAction,
        )
        if (!isPlayedListEmpty) {
            BottomSheetRowUi(
                icon = Icons.Rounded.PlaylistRemove,
                text = strings.removeFromPlayedList,
                onClick = removeFromPlayedListAction,
            )
        }
        BottomSheetRowUi(
            icon = Icons.Rounded.Delete,
            text = strings.deleteSelectedAlbums,
            onClick = deleteAction
        )
    }
}