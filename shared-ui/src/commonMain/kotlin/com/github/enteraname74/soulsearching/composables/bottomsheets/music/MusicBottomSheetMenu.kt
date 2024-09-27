package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetRow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
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
    isCurrentlyPlaying: Boolean,
    soulSearchingSettings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
) {
    val isQuickAccessShown: Boolean = soulSearchingSettings.get(
        settingElement = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN
    )

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(color = SoulSearchingColorTheme.colorScheme.secondary)
            .padding(UiConstants.Spacing.large)
    ) {
        if (isQuickAccessShown) {
            BottomSheetRow(
                icon = Icons.Rounded.DoubleArrow,
                text = if (isInQuickAccess) {
                    strings.removeFromQuickAccess
                } else {
                    strings.addToQuickAccess
                },
                onClick = quickAccessAction,
            )
        }
        BottomSheetRow(
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            text = strings.addToPlaylist,
            onClick = addToPlaylistAction,
        )
        BottomSheetRow(
            icon = Icons.Rounded.Edit,
            text = strings.modifyMusic,
            onClick = modifyAction,
        )
        if (!isCurrentlyPlaying) {
            BottomSheetRow(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                text = strings.playNext,
                onClick = playNextAction,
            )
        }
        if (musicBottomSheetState == MusicBottomSheetState.PLAYLIST) {
            BottomSheetRow(
                icon = Icons.Rounded.Delete,
                text = strings.removeFromPlaylist,
                onClick = removeFromPlaylistAction,
            )
        }
        if (playbackManager.playedList.isNotEmpty()) {
            BottomSheetRow(
                icon = Icons.Rounded.PlaylistRemove,
                text = strings.removeFromPlayedList,
                onClick = removeFromPlayedListAction,
            )
        }
        BottomSheetRow(
            icon = Icons.Rounded.Delete,
            text = strings.deleteMusic,
            onClick = removeAction,
        )
    }
}