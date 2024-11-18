package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
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
    deleteAction : () -> Unit,
    quickAccessAction : () -> Unit,
    isInQuickAccess: Boolean,
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