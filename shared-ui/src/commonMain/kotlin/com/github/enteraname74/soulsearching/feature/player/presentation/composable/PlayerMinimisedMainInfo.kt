package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.MinimisedPlayerControlsComposable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMinimisedMainInfo(
    imageSize: Dp,
    playerViewManager: PlayerViewManager = injectElement(),
    currentMusic: Music?,
    isPlaying: Boolean,
    alphaTransition: Float,
) {

    Row(
        modifier = Modifier
            .height(imageSize + UiConstants.Spacing.small)
            .fillMaxWidth()
            .padding(
                start = imageSize + UiConstants.Spacing.large,
                end = UiConstants.Spacing.small
            )
            .alpha(alphaTransition),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentMusic?.name.orEmpty(),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                maxLines = 1,
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = currentMusic?.artist.orEmpty(),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
        MinimisedPlayerControlsComposable(
            playerViewDraggableState = playerViewManager.playerDraggableState,
            isPlaying = isPlaying,
        )
    }
}