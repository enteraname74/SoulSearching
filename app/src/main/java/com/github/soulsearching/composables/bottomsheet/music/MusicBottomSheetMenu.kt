package com.github.soulsearching.composables.bottomsheet.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.SettingsUtils
import com.github.soulsearching.classes.types.MusicBottomSheetState
import com.github.soulsearching.composables.bottomsheet.BottomSheetRow
import com.github.soulsearching.ui.theme.DynamicColor

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
    primaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary,
    isCurrentlyPlaying: Boolean
) {
    Column(
        modifier = Modifier
            .background(color = primaryColor)
            .padding(Constants.Spacing.large)
    ) {
        if (SettingsUtils.settingsViewModel.isQuickAccessShown) {
            BottomSheetRow(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isInQuickAccess) {
                    stringResource(id = R.string.remove_from_quick_access)
                } else {
                    stringResource(id = R.string.add_to_quick_access)
                },
                onClick = quickAccessAction,
                textColor = textColor
            )
        }
        BottomSheetRow(
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            text = stringResource(id = R.string.add_to_playlist),
            onClick = addToPlaylistAction,
            textColor = textColor
        )
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = stringResource(id = R.string.modify_music),
            onClick = modifyAction,
            textColor = textColor
        )
        if (!isCurrentlyPlaying) {
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = stringResource(id = R.string.play_next),
                onClick = playNextAction,
                textColor = textColor
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYLIST) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = stringResource(id = R.string.remove_from_playlist),
                onClick = removeFromPlaylistAction,
                textColor = textColor
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYER) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = stringResource(id = R.string.remove_from_played_list),
                onClick = removeFromPlayedListAction,
                textColor = textColor
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = stringResource(id = R.string.delete_music),
            onClick = removeAction,
            textColor = textColor
        )
    }
}