package com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MultipleArtistsWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(UiConstants.Spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    min = CardMinWidth,
                    max = CardMaxWidth
                ),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                containerColor = SoulSearchingColorTheme.colorScheme.secondary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulIcon(
                    icon = Icons.Rounded.Groups,
                    size = UiConstants.ImageSize.mediumPlus,
                    tint = SoulSearchingColorTheme.colorScheme.onSecondary,
                )
                Text(
                    text = strings.multipleArtistsText,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.body,
                )
            }
        }
    }
}

private val CardMinWidth: Dp = 0.dp
private val CardMaxWidth: Dp = 500.dp
