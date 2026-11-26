package com.github.enteraname74.soulsearching.feature.player.presentation.screen.views

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.lyrics.LyricsFetchState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import kotlin.uuid.Uuid

@Composable
internal fun PlayerSwipeableLargeScreenView(
    currentMusicProgression: Int,
    state: PlayerViewState.Data,
    playbackCommandsState: PlaybackCommandsState,
    lyricsState: LyricsFetchState,
    onMaxWidthForCover: (maxWidth: Dp) -> Unit,
    onMaxHeightForCover: (maxHeight: Dp) -> Unit,
    showMusicBottomSheet: (musicId: Uuid) -> Unit,
    onLongSelectOnMusic: (Music) -> Unit,
    onSwiped: ((Music) -> Unit)?,
    onClickOnMusic: ((Music) -> Unit)?,
    multiSelectionState: MultiSelectionState,
    animatedBackgroundColor: Color,
    onActivateRemoteLyrics: () -> Unit,
) {
    val playerViewManager: PlayerViewManager = injectElement()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = UiConstants.Spacing.medium,
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
            ) {
                LaunchedEffect(maxWidth) {
                    onMaxWidthForCover(maxWidth)
                }
                LaunchedEffect(maxHeight) {
                    onMaxHeightForCover(maxHeight)
                }
            }
            ExpandedPlayerControlsComposable(
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.medium,
                        bottom = UiConstants.Spacing.medium,
                    ),
                currentMusicProgression = currentMusicProgression,
                state = state,
                playbackCommandsState = playbackCommandsState,
            )
        }
        PlayerPanelContent(
            modifier = Modifier
                .alpha(PlayerUiUtils.getTransitionRatio())
                .weight(.8f),
            playerState = state,
            lyricsState = lyricsState,
            onMoreClickedOnMusic = showMusicBottomSheet,
            contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            containerColor = animatedBackgroundColor,
            subTextColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            isExpanded = playerViewManager.currentValue == BottomSheetStates.EXPANDED,
            buttonColors = SoulButtonDefaults.secondaryColors(),
            onLongSelectOnMusic = onLongSelectOnMusic,
            multiSelectionState = multiSelectionState,
            onActivateRemoteLyrics = onActivateRemoteLyrics,
            onSwiped = onSwiped,
            onClickOnMusic = onClickOnMusic,
        )
    }
}