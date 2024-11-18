package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun AlbumBottomSheetMenu(
    selectedAlbum: AlbumWithMusics,
    modifyAction: () -> Unit,
    playNextAction: () -> Unit,
    deleteAction: () -> Unit,
    quickAccessAction: () -> Unit,
    isInQuickAccess: Boolean,
) {
    Column {
        BottomSheetElementInformation(
            title = selectedAlbum.album.albumName,
            subTitle = selectedAlbum.artist?.artistName.orEmpty(),
            cover = selectedAlbum.cover,
        )

        QuickAccessBottomSheetMenu(
            isElementInQuickAccess = isInQuickAccess,
            quickAccessAction = quickAccessAction,
        ) {
            BottomSheetRow(
                icon = Icons.Rounded.Edit,
                text = strings.modifyAlbum,
                onClick = modifyAction
            )
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
            )
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.deleteAlbum,
                onClick = deleteAction
            )
        }
    }
}