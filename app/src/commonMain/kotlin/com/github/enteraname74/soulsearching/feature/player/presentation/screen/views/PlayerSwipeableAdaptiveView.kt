package com.github.enteraname74.soulsearching.feature.player.presentation.screen.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.lyrics.LyricsFetchState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerUiView
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerTopInformation
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelDraggableView
import org.koin.compose.koinInject
import kotlin.uuid.Uuid

@Composable
internal fun PlayerSwipeableAdaptiveView(
    maxHeight: Float,
    playerUiView: PlayerUiView,
    currentMusicProgression: Int,
    state: PlayerViewState.Data,
    playbackCommandsState: PlaybackCommandsState,
    lyricsState: LyricsFetchState,
    onLongSelectOnMusic: (Music) -> Unit,
    onSwiped: ((Music) -> Unit)?,
    onClickOnMusic: ((Music) -> Unit)?,
    multiSelectionState: MultiSelectionState,
    animatedBackgroundColor: Color,
    onActivateRemoteLyrics: () -> Unit,
    onMaxWidthForCover: (maxWidth: Dp) -> Unit,
    onMaxHeightForCover: (maxHeight: Dp) -> Unit,
    onTopInformationHeight: (height: Dp) -> Unit,
    onArtistClicked: ((selectedArtist: Artist) -> Unit)?,
    onAlbumClicked: (() -> Unit)?,
    showMusicBottomSheet: (musicId: Uuid) -> Unit,
    closeSelection: () -> Unit,
) {
    val playerMusicListViewManager: PlayerMusicListViewManager = koinInject()
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PlayerTopInformation(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        onTopInformationHeight(layoutCoordinates.size.height.toDp(density))
                    },
                state = state,
                onArtistClicked = onArtistClicked,
                onAlbumClicked = onAlbumClicked,
                onShowPanel = {
                    playerMusicListViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }.takeIf {
                    !PlayerUiUtils.canShowSidePanel()
                        && PlayerUiUtils.getDraggablePanelCollapsedOffset() == 0f
                },
                onSongInfoClicked = showMusicBottomSheet.let {
                    { it(state.currentMusic.musicId) }
                }
            )
            when (playerUiView) {
                PlayerUiView.LargeScreen -> PlayerSwipeableLargeScreenView(
                    currentMusicProgression = currentMusicProgression,
                    state = state,
                    playbackCommandsState = playbackCommandsState,
                    lyricsState = lyricsState,
                    onMaxWidthForCover = onMaxWidthForCover,
                    onMaxHeightForCover = onMaxHeightForCover,
                    showMusicBottomSheet = showMusicBottomSheet,
                    onLongSelectOnMusic = onLongSelectOnMusic,
                    onSwiped = onSwiped,
                    onClickOnMusic = onClickOnMusic,
                    multiSelectionState = multiSelectionState,
                    animatedBackgroundColor = animatedBackgroundColor,
                    onActivateRemoteLyrics = onActivateRemoteLyrics,
                )
                PlayerUiView.DefaultMobile -> {
                    PlayerSwipeableDefaultMobileView(
                        currentMusicProgression = currentMusicProgression,
                        state = state,
                        playbackCommandsState = playbackCommandsState,
                        onMaxWidthForCover = onMaxWidthForCover,
                        onMaxHeightForCover = onMaxHeightForCover,
                    )
                }
                PlayerUiView.SmallRow -> PlayerSwipeableRowSmallView(
                    currentMusicProgression = currentMusicProgression,
                    state = state,
                    playbackCommandsState = playbackCommandsState,
                    onMaxWidthForCover = onMaxWidthForCover,
                    onMaxHeightForCover = onMaxHeightForCover,
                )
            }
        }
        if (playerUiView != PlayerUiView.LargeScreen) {
            PlayerPanelDraggableView(
                maxHeight = maxHeight,
                playerState = state,
                lyricsState = lyricsState,
                onMoreClickedOnMusic = showMusicBottomSheet,
                containerColor = SoulSearchingColorTheme.colorScheme.secondary,
                textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                buttonColors = SoulButtonDefaults.primaryColors(),
                multiSelectionState = multiSelectionState,
                onLongSelectOnMusic = onLongSelectOnMusic,
                closeSelection = closeSelection,
                onActivateRemoteLyrics = onActivateRemoteLyrics,
                onSwiped = onSwiped,
                onClickOnMusic = onClickOnMusic,
            )
        }
    }
}