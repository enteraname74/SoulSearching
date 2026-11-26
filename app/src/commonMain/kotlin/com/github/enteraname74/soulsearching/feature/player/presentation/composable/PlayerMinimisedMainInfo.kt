package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.MinimisedPlayerControlsComposable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMinimisedMainInfo(
    imageSize: Dp,
    startPadding: Dp,
    playerViewManager: PlayerViewManager = injectElement(),
    currentMusic: Music,
    alphaTransition: Float,
    playbackCommandsState: PlaybackCommandsState,
    state: PlayerViewState.Data,
) {
    val coverPadding = startPadding + UiConstants.Spacing.large

    Row(
        modifier = Modifier
            .height(imageSize)
            .fillMaxWidth()
            .padding(
                top = UiConstants.Spacing.medium,
                start = coverPadding,
                end = UiConstants.Spacing.small
            )
            .alpha(alphaTransition),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f, fill = PlayerUiUtils.canShowMoreInfoOnMinimisedView()),
            verticalArrangement = Arrangement.Center,
        ) {
            SoulText(
                text = currentMusic.name.toTextSpec(),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                maxLines = 1,
                fontSize = 15.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )
            SoulText(
                text = currentMusic.artistsNames.toTextSpec(),
                color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        MinimisedPlayerControlsComposable(
            modifier = Modifier
                .weight(1f, fill = PlayerUiUtils.canShowMoreInfoOnMinimisedView()),
            playerViewState = playerViewManager.draggableState.currentValue,
            playbackCommandsState = playbackCommandsState,
            state = state,
        )
        if (PlayerUiUtils.canShowMoreInfoOnMinimisedView()) {
            Spacer(
                modifier = Modifier
                    .width(coverPadding)
            )
            PlayerVolume(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .padding(
                        end = UiConstants.Spacing.medium,
                    ),
                playbackCommandsState = playbackCommandsState,
            )
        }
    }
}