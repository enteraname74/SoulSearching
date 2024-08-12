package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

@Composable
fun MusicBottomSheetMenu(
    modifyAction: () -> Unit,
    removeAction: () -> Unit,
    removeFromPlaylistAction: () -> Unit,
    removeFromPlayedListAction: () -> Unit,
    quickAccessAction: () -> Unit,
    addToPlaylistAction: () -> Unit,
    playNextAction : () -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isInQuickAccess: Boolean,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    isCurrentlyPlaying: Boolean,
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
) {
    Column(
        modifier = Modifier
            .background(color = primaryColor)
            .padding(UiConstants.Spacing.large)
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
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
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
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
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
        if (playbackManager.playedList.isNotEmpty()) {
            BottomSheetRow(
                icon = Icons.Rounded.PlaylistRemove,
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