package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun ArtistBottomSheetMenu(
    modifyAction: () -> Unit,
    deleteAction: () -> Unit,
    playNextAction: () -> Unit,
    quickAccessAction: () -> Unit,
    isInQuickAccess: Boolean,
    selectedArtist: ArtistWithMusics,
    isPlayedListEmpty: Boolean,
    removeFromPlayedListAction: () -> Unit,
) {
    Column {
        BottomSheetElementInformation(
            title = selectedArtist.artist.artistName,
            subTitle = strings.musics(total = selectedArtist.musics.size),
            cover = selectedArtist.cover,
        )

        QuickAccessBottomSheetMenu(
            isElementInQuickAccess = isInQuickAccess,
            quickAccessAction = quickAccessAction,
        ) {
            BottomSheetRow(
                icon = Icons.Rounded.Edit,
                text = strings.modifyArtist,
                onClick = modifyAction
            )
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
            )
            if (!isPlayedListEmpty) {
                BottomSheetRow(
                    icon = Icons.Rounded.PlaylistRemove,
                    text = strings.removeFromPlayedList,
                    onClick = removeFromPlayedListAction,
                )
            }
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.deleteArtist,
                onClick = deleteAction
            )
        }
    }
}
