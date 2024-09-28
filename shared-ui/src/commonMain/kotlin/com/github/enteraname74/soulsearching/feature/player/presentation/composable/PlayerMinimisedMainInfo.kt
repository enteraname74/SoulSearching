package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
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
            .height(imageSize)
            .fillMaxWidth()
            .padding(
                top = UiConstants.Spacing.medium,
                start = imageSize + UiConstants.Spacing.large,
                end = UiConstants.Spacing.small
            )
            .alpha(alphaTransition),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill= false),
        ) {
            Text(
                text = currentMusic?.name.orEmpty(),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                maxLines = 1,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = currentMusic?.artist.orEmpty(),
                color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        MinimisedPlayerControlsComposable(
            playerViewState = playerViewManager.playerDraggableState.currentValue,
            isPlaying = isPlaying,
        )
    }
}