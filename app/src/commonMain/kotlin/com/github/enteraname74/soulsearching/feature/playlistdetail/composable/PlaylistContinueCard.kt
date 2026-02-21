package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
internal fun PlaylistContinueCard(
    playedListToContinue: PlayedListToContinue,
    onContinue: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.secondary,
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.medium,
                    vertical = UiConstants.Spacing.small,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            TopInformation(
                onDelete = onDelete,
            )
            CurrentMusicInfo(
                currentMusic = playedListToContinue.currentMusic,
                onContinue = onContinue,
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
        Text(
            modifier = Modifier
                .weight(1f),
            text = strings.continuePlayedListTitle,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.bodyTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        SoulIconButton(
            icon = Icons.Rounded.Close,
            onClick = onDelete,
            colors = SoulButtonDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
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
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = currentMusic.name,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = currentMusic.artists,
                color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SoulIconButton(
            icon = Icons.Rounded.PlayArrow,
            onClick = onContinue,
            colors = SoulButtonDefaults.colors(
                containerColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                contentColor = SoulSearchingColorTheme.colorScheme.secondary,
            )
        )
    }
}