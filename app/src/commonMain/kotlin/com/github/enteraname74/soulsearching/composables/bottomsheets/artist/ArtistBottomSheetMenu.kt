package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetElementInformation
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRowUi
import com.github.enteraname74.soulsearching.composables.bottomsheets.QuickAccessBottomSheetMenu
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun ArtistBottomSheetMenu(
    modifyAction: () -> Unit,
    deleteAction: () -> Unit,
    playNextAction: () -> Unit,
    addToQueueAction: () -> Unit,
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
            BottomSheetRowUi(
                icon = Icons.Rounded.Edit,
                text = strings.modifyArtist,
                onClick = modifyAction
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
                text = strings.deleteArtist,
                onClick = deleteAction
            )
        }
    }
}
