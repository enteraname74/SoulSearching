package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedButton
import com.github.enteraname74.soulsearching.coreui.button.SoulSegmentedIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_shuffle
import com.github.enteraname74.soulsearching.coreui.ext.disableFocus
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun PlaylistPanel(
    editAction: (() -> Unit)?,
    playAction: () -> Unit,
    shuffleAction: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .disableFocus()
            .background(containerColor)
            .padding(vertical = UiConstants.Spacing.mediumPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImagesButton(
            editAction = editAction,
            shuffleAction = shuffleAction,
            playAction = playAction,
            tint = tint,
            primaryColor = secondaryColor
        )
    }

}

@Composable
private fun ImagesButton(
    editAction: (() -> Unit)?,
    playAction: () -> Unit,
    shuffleAction: () -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    SoulSegmentedButton(
        colors = SoulButtonDefaults.colors(
            containerColor = primaryColor,
            contentColor = tint,
        ),
        buttons = buildList {
            editAction?.let { action ->
                add(
                    SoulSegmentedIconButton(
                        data =  CoreRes.drawable.ic_edit_filled,
                        onClick = action,
                        contentPadding = SoulButtonDefaults.contentPadding(),
                    )
                )
            }
            add(
                SoulSegmentedIconButton(
                    data =  CoreRes.drawable.ic_play_filled,
                    onClick = playAction,
                    contentPadding = SoulButtonDefaults.contentPadding(),
                )
            )
            add(
                SoulSegmentedIconButton(
                    data =  CoreRes.drawable.ic_shuffle,
                    onClick = shuffleAction,
                    contentPadding = SoulButtonDefaults.contentPadding(),
                )
            )
        }
    )
}