package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_pause_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_skip_next_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_skip_previous_filled
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.ext.disabledIfNoAction

@Composable
fun MinimisedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    playerViewState: BottomSheetStates,
    isPlaying: Boolean,
    previous: (() -> Unit)?,
    togglePlayPause: (() -> Unit)?,
    next: (() -> Unit)?,
) {
    val isMinimised = playerViewState == BottomSheetStates.MINIMISED

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
    ) {
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_previous_filled,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(previous.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(previous)
        )
        SoulIcon(
            icon = if (isPlaying) {
                CoreRes.drawable.ic_pause_filled
            } else {
                CoreRes.drawable.ic_play_filled
            },
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(togglePlayPause.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(togglePlayPause)
        )
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_next_filled,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(next.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(next)
        )
    }
}