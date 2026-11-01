package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.composable.SoulCard
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.continue_played_list_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_close
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.CachedPlayedListUiSpec
import com.github.enteraname74.soulsearching.domain.model.player.PlayedListToContinue

@Composable
internal fun PlaylistContinueCard(
    spec: CachedPlayedListUiSpec,
    modifier: Modifier = Modifier,
) {
    SoulCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
        ) {
            TopInformation(
                onDelete = spec.onDismiss,
            )
            CurrentMusicInfo(
                currentMusic = spec.currentMusic,
                onContinue = spec.onContinue,
            )
        }
    }
}

@Composable
private fun TopInformation(
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoulText(
            modifier = Modifier
                .weight(1f),
            text = CoreRes.string.continue_played_list_title.toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.bodyTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        SoulIconButton(
            icon = CoreRes.drawable.ic_close,
            onClick = onDelete,
            colors = SoulButtonDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = SoulSearchingColorTheme.colorScheme.text,
            )
        )
    }
}

@Composable
private fun CurrentMusicInfo(
    currentMusic: PlayedListToContinue.CurrentMusic,
    onContinue: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            SoulText(
                text = currentMusic.name.toTextSpec(),
                color = SoulSearchingColorTheme.colorScheme.text,
                style = UiConstants.Typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            SoulText(
                text = currentMusic.artists.toTextSpec(),
                color = SoulSearchingColorTheme.colorScheme.textSecondary,
                style = UiConstants.Typography.body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SoulIconButton(
            modifier = Modifier
                .size(48.dp),
            icon = CoreRes.drawable.ic_play_filled,
            onClick = onContinue,
            colors = SoulButtonDefaults.accentColors()
        )
    }
}
