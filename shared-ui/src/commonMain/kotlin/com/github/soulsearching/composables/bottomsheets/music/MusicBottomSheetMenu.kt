package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.MusicBottomSheetState

@Composable
fun MusicBottomSheetMenu(
    modifyAction: () -> Unit,
    removeAction: () -> Unit,
    removeFromPlaylistAction: () -> Unit = {},
    removeFromPlayedListAction: () -> Unit = {},
    quickAccessAction: () -> Unit,
    addToPlaylistAction: () -> Unit,
    playNextAction : () -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isInQuickAccess: Boolean,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    isCurrentlyPlaying: Boolean,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    Column(
        modifier = Modifier
            .background(color = primaryColor)
            .padding(Constants.Spacing.large)
    ) {
        if (viewSettingsManager.isQuickAccessShown) {
            BottomSheetRow(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isInQuickAccess) {
                    strings.removeFromQuickAccess
                } else {
                    strings.addToQuickAccess
                },
                onClick = quickAccessAction,
                textColor = textColor
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.PlaylistAdd,
            text = strings.addToPlaylist,
            onClick = addToPlaylistAction,
            textColor = textColor
        )
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = strings.modifyMusic,
            onClick = modifyAction,
            textColor = textColor
        )
        if (!isCurrentlyPlaying) {
            BottomSheetRow(
                icon = Icons.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
                textColor = textColor
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYLIST) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.removeFromPlaylist,
                onClick = removeFromPlaylistAction,
                textColor = textColor
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYER) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.removeFromPlayedList,
                onClick = removeFromPlayedListAction,
                textColor = textColor
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = strings.deleteMusic,
            onClick = removeAction,
            textColor = textColor
        )
    }
}