package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.ext.disabledIfNoAction
import com.github.enteraname74.soulsearching.feature.player.ext.icon

@Composable
fun MinimisedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    playerViewState: BottomSheetStates,
    state: PlayerViewState.Data,
    playbackCommandsState: PlaybackCommandsState,
) {
    val isMinimised = playerViewState == BottomSheetStates.MINIMISED

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = UiConstants.Spacing.medium,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        if (PlayerUiUtils.canShowSidePanel()) {
            SoulIcon(
                icon = state.playerMode.icon(),
                modifier = Modifier
                    .size(OPTIONAL_ICON_SIZE)
                    .clip(CircleShape)
                    .optionalClickable(playbackCommandsState.changePlayerMode.takeIf { isMinimised }),
                color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.previous)
            )
        }
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_previous_filled,
            modifier = Modifier
                .size(MAIN_ICON_SIZE)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.previous.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.previous)
        )
        SoulIcon(
            icon = if (playbackCommandsState.isPlaying) {
                CoreRes.drawable.ic_pause_filled
            } else {
                CoreRes.drawable.ic_play_filled
            },
            modifier = Modifier
                .size(MAIN_ICON_SIZE)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.togglePlayPause.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.togglePlayPause)
        )
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_next_filled,
            modifier = Modifier
                .size(MAIN_ICON_SIZE)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.next.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.next)
        )
        if (PlayerUiUtils.canShowSidePanel()) {
            SoulIcon(
                icon = if (state.isCurrentMusicInFavorite) {
                    CoreRes.drawable.ic_favorite_filled
                } else {
                    CoreRes.drawable.ic_favorite
                },
                modifier = Modifier
                    .size(OPTIONAL_ICON_SIZE)
                    .clip(CircleShape)
                    .optionalClickable(playbackCommandsState.toggleFavoriteState.takeIf { isMinimised }),
                color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.previous)
            )
        }
    }
}

private val MAIN_ICON_SIZE: Dp = 40.dp
private val OPTIONAL_ICON_SIZE: Dp = 22.dp
