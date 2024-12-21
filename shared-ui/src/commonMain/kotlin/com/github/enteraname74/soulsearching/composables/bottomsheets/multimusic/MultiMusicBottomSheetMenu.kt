package com.github.enteraname74.soulsearching.composables.bottomsheets.multimusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import java.util.UUID

@Composable
fun MultiMusicBottomSheetMenu(
    total: Int,
    deleteAll: () -> Unit,
    removeFromPlaylistAction: () -> Unit,
    removeFromPlayedListAction: () -> Unit,
    addToPlaylistAction: () -> Unit,
    playNextAction : () -> Unit,
    isPlayedListEmpty: Boolean,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
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

        BottomSheetRow(
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            text = strings.addToPlaylist,
            onClick = addToPlaylistAction,
        )
        BottomSheetRow(
            icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
            text = strings.playNext,
            onClick = playNextAction,
        )
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
            text = strings.deleteSelectedMusics,
            onClick = deleteAll,
        )
    }
}